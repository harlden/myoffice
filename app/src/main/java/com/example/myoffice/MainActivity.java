package com.example.myoffice;

import com.example.myoffice.user_settings.E_SETTINGS_TYPE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.myoffice.databinding.ActivityMainBinding;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.firebase.FirebaseApp;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.Collections;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.firebase.messaging.FirebaseMessaging;

import org.apache.commons.compress.utils.Lists;


public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static SharedPreferences.Editor settings_shared_editor;
    public static String result_string;
    static NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_services, R.id.navigation_other)
                .build();

        navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController);
        NavigationUI.setupWithNavController(binding.navView, navController);

        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setIcon(R.mipmap.ic_launcher);

        if(user_settings.getSettingValue(E_SETTINGS_TYPE.SETTING_TYPE_CHANGE_THEME) == 1)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        }

        FirebaseMessaging.getInstance().getToken().addOnCompleteListener(task ->
        {
            if (task.isSuccessful())
            {
                String token_notification = task.getResult();

                if(token_notification != user_data.GetUserData(user_data.E_USER_DATA.DATA_TOKEN_NOTIFICATION)) {
                    try {
                        user_data.SaveUserData(user_data.E_USER_DATA.DATA_TOKEN_NOTIFICATION, token_notification);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        });
    }

    public static void ShowNotification(Context context, String text, Boolean is_change_notification_setting)
    {
        if(is_change_notification_setting || user_settings.getSettingValue(E_SETTINGS_TYPE.SETTING_TYPE_ENABLE_NOTIFICATIONS) == 1)
        {
            Toast.makeText(context, text, Toast.LENGTH_LONG).show();
        }
    }

    public static void sendPushNotification(String token, String title, String message) {
        new Thread(() -> {
            String accessToken;
            try {
                accessToken = getAccessToken(); // Реализуйте получение токена доступа
            } catch (IOException e) {
                Log.e("FCM Error", "Failed to get access token", e);
                return;
            }

            String json = String.format(
                    "{\"message\":{\"token\":\"%s\",\"notification\":{\"title\":\"%s\",\"body\":\"%s\"}}}",
                    token, title, message
            );

            Log.d("DEBUG", json);
            try {
                URL url = new URL("https://fcm.googleapis.com/v1/projects/office-employee-8b261/messages:send");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setRequestProperty("Authorization", "Bearer " + accessToken);
                conn.setRequestProperty("Priority", "high");
                conn.setDoOutput(true);

                // Отправка данных
                try (OutputStream os = conn.getOutputStream()) {
                    os.write(json.getBytes());
                    os.flush();
                }

                int responseCode = conn.getResponseCode();
                StringBuilder response = new StringBuilder();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                        responseCode == HttpURLConnection.HTTP_OK ? conn.getInputStream() : conn.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                }

                Log.d("FCM Response", response.toString());

            } catch (Exception e) {
                Log.e("FCM Error", "Error sending push notification", e);
            }
        }).start();
    }

    private static String getAccessToken() throws IOException {
        // Содержимое вашего service-client.json в виде строки
        String json = "{\n" +
                "  \"type\": \"service_account\",\n" +
                "  \"project_id\": \"office-employee-8b261\",\n" +
                "  \"private_key_id\": \"73a4ec9894df424d394d04cd8fa6df2def331ea8\",\n" +
                "  \"private_key\": \"-----BEGIN PRIVATE KEY-----\\nMIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC44tWi2wxnW1uI\\n1IhepWf/9tA+qkUSL0bmqz2ep7+oH2sZUhWHcQKpkoN4GduuI6bRCM6uKDyStLbW\\n+bGgaNJ9WPgqa3Uglar7sk1sVu06eKfKj4yWzTt9BJNwrXVMTHi+eYYPCKGCozeg\\nv/Je1ZMM6qOfSVGavG9ksb7io95Shk3mkMWs0hVJ5GqkND0uiocqEhSyLEjQr8hS\\ni9xHJQCmMsdTv0lXwWLVbvvFsaWW2PQQuvOnwVlSolZOjCLJzsPTnLHLGyZctO/p\\n5esxwLxTD1O6ZIbQD1lurmZhOHT+xzUJrOIExgoRGSPvyncBdCZxxumXek8tMMf4\\n/WSt7lplAgMBAAECggEAGqNgGYtLAbdJfHD2xwA4+Em03lqFAiY6pnCEu8+6jvwb\\n37WScYXIVutLsumy26e6WMLWmIQmmhKch2PJgm2n6jR/6Qi7gvrdfE9H1XS+0F5i\\n+o3tOSsPx6b5oVH0pQ4dlYAH3zGkp6BhmvEc+Cn4enNyKLiVH+DHgfAUKy5mEWiW\\np4y2hKkmeW6ci3tlojSv+9Ay4rUTJn45+WAKK2gbgRX3tmZ7VsZgzsmKR1RH6fXb\\nSo6FLAumsargVGfS1LXCsr7tZQvEY2fICT/Zyft6RWyf0jqOQz3031+zXhjNfbIh\\nVi7nMEFhv5OlvM2XZNIY0sUHhmuFZn+/8XMFJ0h54QKBgQDgKAGq+7jFd6rSvNj3\\nExdO/dyqRQdQ7zyU5KKNDXnW5Fom5mbh0jmOo7fZid5ZT1WUjXCNyzkA3DOtNb0f\\ncoHJpzI8qXpRJ449zauACwAkO0tzLRtdfHdiMzh1cK2kMMhb9AlEmNJ9g9JlhAl/\\nlFh5IBZP9xLVbs33sILhCMPatQKBgQDTJquti2HbwTlIP2qI02SFFoBAZH82cENS\\nrCwmzbQH7jARDT5UiV/gvgjtXrBpmAbiTQg55f/RvLQJ8enAiZcI4Zymjmia/qAa\\nOyNp10uBojZxjEk3RG6veQrbnzd/7mPyHQwv9HEu9tZwi2k6fSGY05JBH2EmYBoP\\nwlvxeqNe8QKBgQDVaAE+gezFw0pU3ApMIFbek35UdOekPEAXGR1Q4PWQi1LxGgX1\\nFKpLjL7qUwHnUUqO5aG0vTgGA58HVtQ5blD2ZUHE0y1quhgH6xlODCsUU4dfniAh\\nfvBCA7XKyX3nhbIbKVjxf/VWHfxfPbe1SMx6dPwJIZdCr/1TxS61hBzCyQKBgExc\\nftfLQX+s2F91d7y13nN3GLiw34S7gs1iMLFz0PjRPhbcmD8QU1k+wJG/ncMCVPMA\\ncsolkKe0O9Qg5a1Y1QNEPFfLxq7PCO2Tl5IOZ2MhW9kip9PR7smM181nPjYI66JC\\nuQaau1gFsrWO5WZenIjdctYUWBj9xk8OthuVg4GxAoGAKShKFfszbm0WVPHjc0Dc\\n314us/knd7emiBLAbT0q30H2SdeFnyN0ES5CfVyawDLhs0VHFVyAxWKKZZi8MM/1\\nv/dAgLrStzE8tzXr7FSEtCvWRCFpAc+q1Dq05WmWCrHNsaDFJLoQ/8g1Sxa5DWXw\\nvHeKcM2ejXuQlLpY5YGdtYY=\\n-----END PRIVATE KEY-----\\n\",\n" +
                "  \"client_email\": \"firebase-adminsdk-fbsvc@office-employee-8b261.iam.gserviceaccount.com\",\n" +
                "  \"client_id\": \"102243056483367951625\",\n" +
                "  \"auth_uri\": \"https://accounts.google.com/o/oauth2/auth\",\n" +
                "  \"token_uri\": \"https://oauth2.googleapis.com/token\",\n" +
                "  \"auth_provider_x509_cert_url\": \"https://www.googleapis.com/oauth2/v1/certs\",\n" +
                "  \"client_x509_cert_url\": \"https://www.googleapis.com/robot/v1/metadata/x509/firebase-adminsdk-fbsvc%40office-employee-8b261.iam.gserviceaccount.com\",\n" +
                "  \"universe_domain\": \"googleapis.com\"\n" +
                "}\n"; // Замените на ваше содержимое

        // Преобразуем строку в поток
        try (ByteArrayInputStream serviceAccountStream = new ByteArrayInputStream(json.getBytes())) {
            GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream)
                    .createScoped(Collections.singleton("https://www.googleapis.com/auth/cloud-platform"));

            credentials.refreshIfExpired();
            return credentials.getAccessToken().getTokenValue();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        return navController.navigateUp() || super.onSupportNavigateUp();
    }
}