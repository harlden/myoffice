package com.example.myoffice;


import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.w3c.dom.Text;

import java.lang.ref.WeakReference;
import java.sql.SQLException;

public class Registration extends AppCompatActivity {
    private EditText login, password, key;
    private TextInputLayout layout_login, layout_password, layout_key;
    private TextView go_login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setContentView(R.layout.registration);

        login = findViewById(R.id.registration_login);
        layout_login = findViewById(R.id.textInputLayoutRegistrationLogin);

        password = findViewById(R.id.registration_password);
        layout_password = findViewById(R.id.textInputLayoutRegistrationPassword);

        key = findViewById(R.id.registration_key);
        layout_key = findViewById(R.id.textInputLayoutRegistrationKey);

        Button registrationButton = findViewById(R.id.registration_button);
        go_login = findViewById(R.id.registration_go_login);

        go_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Login.playClickSound(Registration.this, "click_move");
                Intent intent = new Intent(Registration.this, Login.class);
                startActivity(intent);
                finish(); // Закрыть LoginActivity, чтобы не вернуться к ней
            }
        });

        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Login.playClickSound(Registration.this, "click_move");
                if(!shouldShowErrorLogin() && !shouldShowErrorPassword() && !shouldShowErrorKey())
                {
                    DataBase DataBaseClass = new DataBase();
                    DataBase.connection = DataBaseClass.connectSQL();

                    if(DataBase.connection != null)
                    {
                        int code = Integer.parseInt(key.getText().toString().trim());

                        try
                        {
                            if(user_data.IsValidateCode(code))
                            {
                                if(user_data.IsValidateLogin(login.getText().toString()))
                                {
                                    FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->
                                    {
                                        if (task.isSuccessful())
                                        {
                                            String token_notification = task.getResult();

                                            try
                                            {
                                                if(user_data.AddUserData(login.getText().toString(), password.getText().toString(), code, token_notification)) {
                                                    Intent intent = new Intent(Registration.this, Login.class);
                                                    startActivity(intent);
                                                    finish();

                                                    Toast.makeText(getApplicationContext(), "Вы успешно зарегистрировались", Toast.LENGTH_LONG).show();
                                                }
                                                else
                                                {
                                                    Toast.makeText(getApplicationContext(), "Пользователь не найден", Toast.LENGTH_LONG).show();
                                                }
                                            } catch (SQLException e) {
                                                throw new RuntimeException(e);
                                            }
                                        }
                                    });
                                }
                                else layout_login.setError("Логин занят");
                            }
                            else layout_key.setError("Ключ доступа не найден");
                        }
                        catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "Отсутствует подключение к интернету", Toast.LENGTH_LONG).show();
                    }
                }
                else
                {
                    if(shouldShowErrorLogin())
                        showErrorLogin();

                    if(shouldShowErrorPassword())
                        showErrorPassword();

                    if(shouldShowErrorKey())
                        showErrorKey();
                }
            }
        });

        layout_login.setHint("Введите логин");
        login.setOnEditorActionListener(Registration.ActionListener.newInstance(this));

        layout_password.setHint("Введите пароль");
        password.setOnEditorActionListener(Registration.ActionListener.newInstance(this));

        layout_key.setHint("Введите ключ доступа");
        key.setOnEditorActionListener(Registration.ActionListener.newInstance(this));
    }

    private boolean shouldShowErrorPassword() {
        int textLength = password.getText().length();
        return (textLength > 0 && textLength < 6) || textLength == 0;
    }

    private boolean shouldShowErrorLogin() {
        int textLength = login.getText().length();
        return (textLength > 0 && textLength < 6) || textLength == 0;
    }

    private boolean shouldShowErrorKey() {
        int textLength = key.getText().length();
        return (textLength > 0 && textLength < 4) || textLength == 0;
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

    private void showErrorKey() {
        layout_key.setError("Ключ доступа должен быть не менее 4 символов");
    }

    private void hideErrorKey() {
        layout_key.setError("");
    }

    private static final class ActionListener implements TextView.OnEditorActionListener {
        private final WeakReference<Registration> registrationWeakReference;

        public static ActionListener newInstance(Registration registration) {
            WeakReference<Registration> registrationWeakReference = new WeakReference<>(registration);
            return new ActionListener(registrationWeakReference);
        }

        private ActionListener(WeakReference<Registration> registrationWeakReference) {
            this.registrationWeakReference = registrationWeakReference;
        }

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            Registration registration = registrationWeakReference.get();

            if (registration != null)
            {
                if (actionId == EditorInfo.IME_ACTION_GO && registration.shouldShowErrorPassword()) {
                    registration.showErrorPassword();
                }
                else {
                    registration.hideErrorPassword();
                }

                if (actionId == EditorInfo.IME_ACTION_GO && registration.shouldShowErrorLogin()) {
                    registration.showErrorLogin();
                }
                else {
                    registration.hideErrorLogin();
                }

                if (actionId == EditorInfo.IME_ACTION_GO && registration.shouldShowErrorKey()) {
                    registration.showErrorKey();
                }
                else {
                    registration.hideErrorKey();
                }
            }
            return true;
        }
    }
}