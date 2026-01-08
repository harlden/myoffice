package com.example.myoffice;

import android.util.Log;

import java.util.Arrays;
import java.util.Locale;

public class user_settings
{
    public enum E_SETTINGS_TYPE
    {
        SETTING_TYPE_CHANGE_THEME,
        SETTING_TYPE_ENABLE_SOUND,
        SETTING_TYPE_ENABLE_NOTIFICATIONS,
        SETTING_TYPE_IS_REMEMBER_ME
    }

    static final int MAX_SETTINGS = 4;

    public static String[] settings_name = new String[] {"Тёмная тема", "Звуки", "Уведомления", ""};
    public static String[] settings_file_name = new String[] {"is_enable_night", "is_enable_sound", "is_enable_notifications", "is_remember_me"};
    public static String[] settings_value_status = new String[] {"Выключено", "Выключено", "Выключено", "Выключено"};
    public static String[] settings_remember_data = new String[2];
    public static int[] settings_value = new int[MAX_SETTINGS];
    public static int[] setting_default_value = new int[] {0, 1, 1, 0};

    public static String[] getArraySettingsName() {
        return Arrays.stream(settings_name)
                .filter(s -> !s.isEmpty())  // Фильтрация пустых строк
                .toArray(String[]::new);    // Преобразование обратно в массив
    }

    public static String[] getArraySettingsStatus()
    {
        return settings_value_status;
    }

    public static int getSettingValue(E_SETTINGS_TYPE index_setting)
    {
        return settings_value[index_setting.ordinal()];
    }

    public static void LoadSettings()
    {
        for (int i = 0; i < MAX_SETTINGS; i++)
        {
            settings_value[i] = Login.settings_shared.getInt(settings_file_name[i], setting_default_value[i]);
            settings_value_status[i] = settings_value[i] == 1 ? "Включено" : "Выключено";
        }

        settings_remember_data[0] = Login.settings_shared.getString("remember_login", "");
        settings_remember_data[1] = Login.settings_shared.getString("remember_password", "");

        Log.d("DEBUG", String.format("%s %s", settings_remember_data[0], settings_remember_data[1]));
        Log.d("DEBUG", String.format("is_remember_me %d", getSettingValue(E_SETTINGS_TYPE.SETTING_TYPE_IS_REMEMBER_ME)));
    }

    public static void SaveSettings(E_SETTINGS_TYPE index_setting, int value)
    {
        MainActivity.settings_shared_editor = Login.settings_shared.edit();

        MainActivity.settings_shared_editor.putInt(settings_file_name[index_setting.ordinal()], value);
        MainActivity.settings_shared_editor.apply();

        settings_value[index_setting.ordinal()] = value;
        settings_value_status[index_setting.ordinal()] = settings_value[index_setting.ordinal()] == 1 ? "Включено" : "Выключено";
    }

    public static void SaveRememberData(String login, String password, int is_remember_me)
    {
        MainActivity.settings_shared_editor = Login.settings_shared.edit();

        MainActivity.settings_shared_editor.putInt("is_remember_me", is_remember_me);
        MainActivity.settings_shared_editor.putString("remember_login", login);
        MainActivity.settings_shared_editor.putString("remember_password", password);
        MainActivity.settings_shared_editor.apply();

        settings_value[E_SETTINGS_TYPE.SETTING_TYPE_IS_REMEMBER_ME.ordinal()] = 1;
        settings_remember_data[0] = login;
        settings_remember_data[1] = password;
    }
}
