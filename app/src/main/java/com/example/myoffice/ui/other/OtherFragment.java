package com.example.myoffice.ui.other;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.example.myoffice.EditTextDialog;
import com.example.myoffice.EditTextItem;
import com.example.myoffice.Login;
import com.example.myoffice.MainActivity;
import com.example.myoffice.Registration;
import com.example.myoffice.ui.services.classes.ReportGenerator;
import com.example.myoffice.ui.services.services_pages.ServiceEmployers;
import com.example.myoffice.user_data;
import com.example.myoffice.user_settings;
import com.example.myoffice.R;
import com.example.myoffice.databinding.FragmentOtherBinding;

import java.sql.SQLException;
import java.util.List;
import java.util.Locale;


public class OtherFragment extends Fragment {
    private FragmentOtherBinding binding;
    private EditTextItem[] items;

    @SuppressLint("SetTextI18n")
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) 
    {
        binding = FragmentOtherBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        ListView settings_listview = binding.settingsListview;

        ArrayAdapter<String> settings_adapter = new ArrayAdapter<>(requireContext(), R.layout.list_settings, R.id.name_setting, user_settings.getArraySettingsName());
        settings_listview.setAdapter(settings_adapter);

        settings_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Login.playClickSound(requireContext(), "click_tap");
                user_settings.E_SETTINGS_TYPE[] items = user_settings.E_SETTINGS_TYPE.values();
                user_settings.E_SETTINGS_TYPE select_item = items[i];

                int is_enable = user_settings.getSettingValue(select_item);

                switch (select_item)
                {
                    case SETTING_TYPE_CHANGE_THEME:
                        ChangeTheme();
                        break;

                    case SETTING_TYPE_ENABLE_SOUND:
                        user_settings.SaveSettings(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_ENABLE_SOUND,
                                (is_enable == 1) ? 0 : 1);
                        break;

                    case SETTING_TYPE_ENABLE_NOTIFICATIONS:
                        user_settings.SaveSettings(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_ENABLE_NOTIFICATIONS,
                                (is_enable == 1) ? 0 : 1);
                        break;
                }

                ShowNotificationChangeSetting(select_item, select_item == user_settings.E_SETTINGS_TYPE.SETTING_TYPE_ENABLE_NOTIFICATIONS);
            }
        });

        UpdateFioAndJobTitle();

        ImageButton exit_account = binding.exitAccount;

        exit_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Подтверждение выхода")
                        .setMessage("Вы уверены, что хотите выйти из профиля?")
                        .setPositiveButton("Выйти", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Login.playClickSound(requireContext(), "click_move");
                                Intent intent = new Intent(requireContext(), Login.class);
                                startActivity(intent);

                                onDestroy(); // Закрыть LoginActivity, чтобы не вернуться к ней
                                Toast.makeText(requireContext(), "Вы успешно вышли из профиля", Toast.LENGTH_LONG).show();

                                user_settings.SaveRememberData("", "", 0);
                            }
                        })
                        .setNegativeButton("Закрыть", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss(); // Закрыть диалог
                            }
                        })
                        .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                        .show();
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void ChangeTheme()
    {
        if(user_settings.getSettingValue(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_CHANGE_THEME) == 1)
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            user_settings.SaveSettings(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_CHANGE_THEME, 0);
        }
        else
        {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            user_settings.SaveSettings(user_settings.E_SETTINGS_TYPE.SETTING_TYPE_CHANGE_THEME, 1);
        }
    }

    public void ShowNotificationChangeSetting(user_settings.E_SETTINGS_TYPE index_setting, boolean is_change_notification)
    {
        int is_enable = user_settings.getSettingValue(index_setting);

        String out_text = String.format("Вы успешно %s параметр `%s`.",
                 is_enable == 1 ? "включили" : "выключили", user_settings.settings_name[index_setting.ordinal()]);

        MainActivity.ShowNotification(requireContext(), out_text, is_change_notification);
    }

    public void ShowDialogEditFioAccount()
    {
        Login.playClickSound(requireContext(), "click_tap");
        String job_title = user_data.GetJobTitle((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK));

        items = new EditTextItem[]
        {
                new EditTextItem("Фамилия", "", String.valueOf(user_data.GetUserData(user_data.E_USER_DATA.DATA_SURNAME)), false, false),
                new EditTextItem("Имя", "", String.valueOf(user_data.GetUserData(user_data.E_USER_DATA.DATA_NAME)), false, false),
                new EditTextItem("Должность", "", List.of(user_data.job_name), false, user_data.GetJobRank(job_title), false)
        };

        EditTextDialog editTextDialog = new EditTextDialog(requireContext(), items, this::handleDataSubmit);
        editTextDialog.show();
    }

    private boolean handleDataSubmit(List<String> strings) throws SQLException
    {
        String surname = "", name = "";
        int job_rank = -1;

        for (int i = 0; i < strings.size(); i++)
        {
            String input = strings.get(i);
            String hint = items[i].getDescription();

            switch (hint)
            {
                case "Фамилия":
                    if(ServiceEmployers.IsValidSurname(requireContext(), input))
                    {
                        surname = input;
                    }
                    else return false;
                    break;
                case "Имя":
                    if(ServiceEmployers.IsValidName(requireContext(), input)){
                        name = input;
                    }
                    else return false;
                    break;

                case "Должность":
                    if(ServiceEmployers.IsValidJobTitle(requireContext(), input))
                    {
                        job_rank = user_data.GetJobRank(input);
                    }
                    else return false;
                    break;

                default:
                    break;
            }
        }

        if(!surname.isEmpty() && !name.isEmpty() && job_rank != -1)
        {
            user_data.SaveUserData(user_data.E_USER_DATA.DATA_NAME, name);
            user_data.SaveUserData(user_data.E_USER_DATA.DATA_SURNAME, surname);
            user_data.SaveUserData(user_data.E_USER_DATA.DATA_JOB_RANK, job_rank);

            UpdateFioAndJobTitle();
            Login.playClickSound(requireContext(), "click_tap");

            Toast.makeText(requireContext(), "Вы изменили информацию об аккаунте", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void UpdateFioAndJobTitle()
    {
        binding.fioAccount.setText(String.valueOf(user_data.GetUserData(user_data.E_USER_DATA.DATA_NAME)));
        binding.jobTitleAccount.setText(user_data.GetJobTitle((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK)));
    }
}