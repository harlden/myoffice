package com.example.myoffice;


import static androidx.core.content.ContentProviderCompat.requireContext;

import static com.google.gson.internal.$Gson$Types.arrayOf;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

public class Login extends AppCompatActivity {
    private EditText login, password;
    private TextInputLayout layout_login, layout_password;
    private TextView go_registration;
    public static MediaPlayer mediaPlayer;
    public static SharedPreferences settings_shared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(Login.this);
        setContentView(R.layout.login);

        settings_shared = getSharedPreferences("user_settings", Context.MODE_PRIVATE);
        user_settings.LoadSettings();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

        NotificationChannel channel = new NotificationChannel("default_channel", "OfficeEmployer", NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            channel.setAllowBubbles(true);
        }

        notificationManager.createNotificationChannel(channel);

        DataBase DataBaseClass = new DataBase();
        DataBase.connection = DataBaseClass.connectSQL();

        if(user_settings.getSettingValue(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_IS_REMEMBER_ME) == 0) {
            login = findViewById(R.id.login_login);
            layout_login = findViewById(R.id.textInputLayoutLogin);

            password = findViewById(R.id.login_password);
            layout_password = findViewById(R.id.textInputLayoutPassword);

            Button loginButton = findViewById(R.id.login_button);
            go_registration = findViewById(R.id.login_go_registration);

            CheckBox checkbox_remember_me = findViewById(R.id.login_remember_me);

            go_registration.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Login.playClickSound(Login.this, "click_move");
                    Intent intent = new Intent(Login.this, Registration.class);
                    startActivity(intent);
                    finish(); // Закрыть LoginActivity, чтобы не вернуться к ней
                }
            });

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Login.playClickSound(Login.this, "click_move");

                    if (!shouldShowErrorLogin() && !shouldShowErrorPassword())
                    {

                        if (DataBase.connection != null)
                        {
                            try
                            {
                                if (user_data.LoadUserData(login.getText().toString(), password.getText().toString()))
                                {
                                    if(checkbox_remember_me.isChecked())
                                        user_settings.SaveRememberData(login.getText().toString(), password.getText().toString(), 1);

                                    Intent intent = new Intent(Login.this, MainActivity.class);
                                    startActivity(intent);
                                    finish(); // Закрыть LoginActivity, чтобы не вернуться к ней

                                    Toast.makeText(getApplicationContext(), "Успешная авторизация", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(getApplicationContext(), "Пользователь не найден", Toast.LENGTH_LONG).show();
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        if (shouldShowErrorLogin())
                            showErrorLogin();

                        if (shouldShowErrorPassword())
                            showErrorPassword();
                    }
                }
            });

            layout_login.setHint("Введите логин");
            login.setOnEditorActionListener(ActionListener.newInstance(this));

            layout_password.setHint("Введите пароль");
            password.setOnEditorActionListener(ActionListener.newInstance(this));
        }
        else
        {
            if (DataBase.connection != null)
            {
                try
                {
                    if (user_data.LoadUserData(user_settings.settings_remember_data[0], user_settings.settings_remember_data[1]))
                    {
                        Intent intent = new Intent(Login.this, MainActivity.class);
                        startActivity(intent);
                        finish(); // Закрыть LoginActivity, чтобы не вернуться к ней

                        Toast.makeText(getApplicationContext(), "Успешная авторизация", Toast.LENGTH_LONG).show();
                    }
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Toast.makeText(getApplicationContext(), "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static void playClickSound(Context context, String soundName)
    {
        if(user_settings.getSettingValue(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_ENABLE_SOUND) == 1) {
            // Освобождение ресурсов, если mediaPlayer уже существует
            if (mediaPlayer != null) {
                mediaPlayer.release();
            }

            // Получаем ресурс по имени
            int resId = context.getResources().getIdentifier(soundName, "raw", context.getPackageName());
            mediaPlayer = MediaPlayer.create(context, resId); // Создаем MediaPlayer
            mediaPlayer.setVolume(0.1f, 0.1f);
            mediaPlayer.start(); // Воспроизведение звука

            // Освобождение ресурсов после завершения воспроизведения
            mediaPlayer.setOnCompletionListener(mp -> {
                mp.release(); // Освобождение ресурсов
                mediaPlayer = null; // Обнуление ссылки
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release(); // Освобождение ресурсов
            mediaPlayer = null;
        }
    }


    private boolean shouldShowErrorPassword() {
        int textLength = password.getText().length();
        return (textLength > 0 && textLength < 6) || textLength == 0;
    }

    private boolean shouldShowErrorLogin() {
        int textLength = login.getText().length();
        return (textLength > 0 && textLength < 6) || textLength == 0;
    }

    private void showErrorPassword() {
        layout_password.setError("Пароль должен быть не менее 6 символов");
    }

    private void hideErrorPassword() {
        layout_password.setError("");
    }

    private void showErrorLogin() {
        layout_login.setError("Логин должен быть не менее 6 символов");
    }

    private void hideErrorLogin() {
        layout_login.setError("");
    }

    private static final class ActionListener implements TextView.OnEditorActionListener {
        private final WeakReference<Login> loginWeakReference;

        public static ActionListener newInstance(Login login) {
            WeakReference<Login> loginWeakReference = new WeakReference<>(login);
            return new ActionListener(loginWeakReference);
        }

        private ActionListener(WeakReference<Login> mainActivityWeakReference) {
            this.loginWeakReference = mainActivityWeakReference;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Login login = loginWeakReference.get();

            if (login != null)
            {
                if (actionId == EditorInfo.IME_ACTION_GO && login.shouldShowErrorPassword()) {
                    login.showErrorPassword();
                }
                else {
                    login.hideErrorPassword();
                }

                if (actionId == EditorInfo.IME_ACTION_GO && login.shouldShowErrorLogin()) {
                    login.showErrorLogin();
                }
                else {
                    login.hideErrorLogin();
                }
            }
            return true;
        }
    }
}