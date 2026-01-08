package com.example.myoffice.ui.services.fragments;

import static com.example.myoffice.ui.services.fragments.ServiceMain.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myoffice.DataBase;
import com.example.myoffice.Login;
import com.example.myoffice.MainActivity;
import com.example.myoffice.R;
import com.example.myoffice.databinding.ServiceLateInputBinding;
import com.example.myoffice.databinding.ServiceLatesBinding;
import com.example.myoffice.databinding.ServiceMainBinding;
import com.example.myoffice.databinding.ServiceMessageInfoBinding;
import com.example.myoffice.databinding.ServiceMessageInputBinding;
import com.example.myoffice.databinding.ServiceMessagesBinding;
import com.example.myoffice.databinding.ServiceStatementConfirmBinding;
import com.example.myoffice.databinding.ServiceStatementInputBinding;
import com.example.myoffice.ui.services.classes.MessageClass;
import com.example.myoffice.ui.services.classes.ReportGenerator;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.services_pages.ServiceArchive;
import com.example.myoffice.ui.services.services_pages.ServiceStatements;
import com.example.myoffice.user_data;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import org.w3c.dom.Text;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceAdditional extends Fragment {
    public ServiceMainBinding binding;
    public ServiceStatementConfirmBinding statementConfirmBinding;
    public ServiceStatementInputBinding statementInputBinding;
    public ServiceMessageInfoBinding messageInfoBinding;
    public ServiceMessageInputBinding messageInputBinding;
    public ServiceLateInputBinding lateInputBinding;
    public Context context;

    public enum E_SERVICE_ADDITIONAL_TYPE
    {
        SERVICE_ADDITIONAL_TYPE_STATEMENT_CONFIRM,
        SERVICE_ADDITIONAL_TYPE_STATEMENT_INPUT,
        SERVICE_ADDITIONAL_TYPE_MESSAGE_INFO,
        SERVICE_ADDITIONAL_TYPE_MESSAGE_INPUT,
        SERVICE_ADDITIONAL_TYPE_LATE_INPUT,
        SERVICE_ADDITIONAL_TYPE_COUNT,
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        this.context = requireContext();

        binding = ServiceMainBinding.inflate(inflater, container, false);
        statementConfirmBinding = ServiceStatementConfirmBinding.inflate(inflater, container, false);
        statementInputBinding = ServiceStatementInputBinding.inflate(inflater, container, false);
        messageInfoBinding = ServiceMessageInfoBinding.inflate(inflater, container, false);
        messageInputBinding = ServiceMessageInputBinding.inflate(inflater, container, false);
        lateInputBinding = ServiceLateInputBinding.inflate(inflater, container, false);

        View root = binding.getRoot();

        int argument_value = getArguments().getInt("SERVICE_ADDITIONAL_TYPE");
        E_SERVICE_ADDITIONAL_TYPE service_additional_type = E_SERVICE_ADDITIONAL_TYPE.values()[argument_value];

        int accountid;

        switch(service_additional_type)
        {
            case SERVICE_ADDITIONAL_TYPE_STATEMENT_CONFIRM:
                root = statementConfirmBinding.getRoot();

                int statementID = getArguments().getInt("STATEMENT_ID");
                accountid = getArguments().getInt("STATEMENT_ACCOUNT_ID");
                String statement_name = getArguments().getString("STATEMENT_NAME");
                String full_name = getArguments().getString("STATEMENT_FULL_NAME");
                String comment = getArguments().getString("STATEMENT_COMMENT");
                String date_start = getArguments().getString("STATEMENT_DATE_START");
                String date_end = getArguments().getString("STATEMENT_DATE_END");
                String date_create = getArguments().getString("STATEMENT_DATE_CREATE");
                String token_notification = getArguments().getString("STATEMENT_TOKEN_NOTIFICATION");

                TextView statement_id = statementConfirmBinding.documentID;
                TextView statement_date_create = statementConfirmBinding.documentDateCreate;
                TextView statement_type = statementConfirmBinding.documentType;
                TextView fullname = statementConfirmBinding.employee;
                TextView dates = statementConfirmBinding.dates;
                TextView comments = statementConfirmBinding.comment;

                statement_id.setText("№" + statementID);
                statement_type.setText(statement_name);
                fullname.setText(full_name);
                dates.setText("с " + date_start + " по " + date_end);
                comments.setText(comment);
                statement_date_create.setText(date_create);

                Button approve_button = statementConfirmBinding.approveButton;
                approve_button.setOnClickListener(v -> showConfirmAccept(statementID, token_notification, accountid, statement_name, date_start, date_end, comment, full_name));

                Button cancel_button = statementConfirmBinding.rejectButton;
                cancel_button.setOnClickListener(v -> showConfirmCancel(statementID, token_notification, full_name));
                break;

            case SERVICE_ADDITIONAL_TYPE_STATEMENT_INPUT:
                root = statementInputBinding.getRoot();

                accountid = getArguments().getInt("STATEMENT_INPUT_ACCOUNT_ID");

                Spinner document_type_spinner = statementInputBinding.documentTypeSpinner;
                EditText edittext_date_start = statementInputBinding.startDate;
                EditText edittext_date_end = statementInputBinding.endDate;
                EditText edittext_comment = statementInputBinding.comment;
                Button send_button = statementInputBinding.sendButton;

                Spinner documentTypeSpinner = statementInputBinding.documentTypeSpinner;

                // Создание и установка адаптера
                documentTypeSpinner.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, StatementClass.statements_name));

                send_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Login.playClickSound(requireContext(), "click_tap");
                        SendNewStatement(accountid, document_type_spinner.getSelectedItem().toString(), edittext_date_start.getText().toString(),
                                edittext_date_end.getText().toString(), edittext_comment.getText().toString());
                    }
                });

                edittext_date_start.setOnClickListener(v -> showDatePickerDialog(requireContext(), edittext_date_start));
                edittext_date_end.setOnClickListener(v -> showDatePickerDialog(requireContext(), edittext_date_end));
                break;
            case SERVICE_ADDITIONAL_TYPE_MESSAGE_INFO:
                root = messageInfoBinding.getRoot();

                int messageid = getArguments().getInt("MESSAGE_ID");
                int is_read = getArguments().getInt("MESSAGE_IS_READ");

                String title = getArguments().getString("MESSAGE_TITLE");
                String message = getArguments().getString("MESSAGE");
                String send_full_name = getArguments().getString("MESSAGE_SEND_FULL_NAME");
                String send_date = getArguments().getString("MESSAGE_SEND_DATE");

                TextView text_messageid = messageInfoBinding.messageID;
                TextView text_title = messageInfoBinding.messageTitle;
                TextView text_message = messageInfoBinding.message;
                TextView text_send_full_name = messageInfoBinding.messageSendFio;
                TextView text_send_date = messageInfoBinding.messageSendDate;

                text_messageid.setText("№" + messageid);
                text_title.setText(title);
                text_message.setText(message);
                text_send_full_name.setText(send_full_name);
                text_send_date.setText(send_date);

                if(is_read == 0) {
                    try {
                        OnReadMessage(messageid);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }
                break;
            case SERVICE_ADDITIONAL_TYPE_MESSAGE_INPUT:
                root = messageInputBinding.getRoot();

                accountid = getArguments().getInt("MESSAGE_INPUT_ACCOUNT_ID");

                ChipGroup chipGroup = messageInputBinding.messageSelectEmployers;
                Spinner message_type = messageInputBinding.messageSelectType;

                EditText edittext_message_title = messageInputBinding.messageTitle;
                EditText edittext_message = messageInputBinding.message;
                Button send_button_input = messageInputBinding.sendButton;

                List<String> employers_all = new ArrayList<>();

                try {
                    user_data.LoadAllEmployers(employers_all, true);

                    for (String employer : employers_all) {
                        Chip chip = new Chip(requireContext());
                        chip.setText(employer);
                        chip.setCheckable(true);
                        chipGroup.addView(chip);
                    }

                    message_type.setAdapter(new ArrayAdapter<>(context, android.R.layout.simple_spinner_dropdown_item, MessageClass.messages_name));

                    message_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            if(position != MessageClass.E_MESSAGE_TYPE.MESSAGE_TYPE_OTHER.ordinal()) {
                                edittext_message_title.setText(MessageClass.messages_name[position]);
                                edittext_message_title.setEnabled(false);
                            }
                            else
                            {
                                edittext_message_title.setText("");
                                edittext_message_title.setEnabled(true);
                            }
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });

                    send_button_input.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v)
                        {
                            Login.playClickSound(requireContext(), "click_tap");

                            List<String> selectedChips = new ArrayList<>();
                            for (int i = 0; i < chipGroup.getChildCount(); i++) {
                                Chip chip = (Chip) chipGroup.getChildAt(i);
                                if (chip.isChecked()) {
                                    selectedChips.add(chip.getText().toString());
                                }
                            }

                            if (selectedChips.isEmpty())
                            {
                                Toast.makeText(requireContext(), "Выберите хотя бы одного сотрудника", Toast.LENGTH_SHORT).show();
                            }
                            else
                            {
                                if(IsValidTitle(edittext_message_title.getText().toString()) && IsValidMessage((edittext_message.getText().toString())))
                                {
                                    showConfirmSendMessage(accountid, message_type.getSelectedItemPosition(), selectedChips, edittext_message_title.getText().toString(), edittext_message.getText().toString());
                                }
                            }
                        }
                    });
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
                break;
            case SERVICE_ADDITIONAL_TYPE_LATE_INPUT:
                root = lateInputBinding.getRoot();

                accountid = getArguments().getInt("LATE_INPUT_ACCOUNT_ID");

                EditText edittext_date_late = lateInputBinding.dateLate;
                EditText edittext_time_late = lateInputBinding.timeLate;
                EditText edittext_comment_late = lateInputBinding.comment;
                Button send_button_late = lateInputBinding.sendButton;

                send_button_late.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Login.playClickSound(requireContext(), "click_tap");
                        SendNewLate(accountid, edittext_date_late.getText().toString(), edittext_time_late.getText().toString(), edittext_comment_late.getText().toString());
                    }
                });

                edittext_date_late.setOnClickListener(v -> showDatePickerDialog(requireContext(), edittext_date_late));
                edittext_time_late.setOnClickListener(v -> showTimePickerDialog(requireContext(), edittext_time_late));
                break;
        }

        return root;
    }

    public static void showDatePickerDialog(Context context, EditText editText) {
        // Получаем текущую дату
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        // Создаем диалог выбора даты
        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                (DatePicker view, int selectedYear, int selectedMonth, int selectedDay) ->
                {
                    view.setMinDate(System.currentTimeMillis() - 1000); // нельзя выбрать прошлое
                    view.setMaxDate(System.currentTimeMillis() + 31536000000L); // +1 год
                    editText.setText(String.format(Locale.US, "%04d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay));
                }, year, month, day);

        datePickerDialog.show();
    }

    public static void showTimePickerDialog(Context context, EditText editText)
    {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                context,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        // Обработка выбранного времени
                        editText.setText(String.format(Locale.US, "%02d:%02d:00", hourOfDay, minute));
                    }
                },
                0,  // Начальный час
                0,   // Начальные минуты
                true // 24-часовой формат
        );

        timePickerDialog.show();
    }

    private boolean SendNewStatement(int accountid, String statement_name, String date_start, String date_end, String comment)
    {
        if(!IsValidStatementName(statement_name))
            return false;

        if(!IsValidDate(context, date_start))
            return false;

        if(!IsValidDate(context, date_end))
            return false;

        if(!IsValidComment(comment))
            return false;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение отправки")
                .setMessage("Вы уверены, что хотите отправить на согласование заявление?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            if(OnAddStatement(accountid, statement_name, date_start, date_end, comment))
                            {
                                NavController navController = NavHostFragment.findNavController(fragment);
                                Bundle bundle = new Bundle();

                                NavOptions navOptions = new NavOptions.Builder()
                                        .setEnterAnim(R.anim.fade_in)
                                        .setExitAnim(R.anim.slide_out_left)
                                        .setPopEnterAnim(R.anim.fade_in)
                                        .setPopExitAnim(R.anim.fade_out)
                                        .build();

                                navController.navigate(R.id.action_service_additional_to_navigation_services, bundle, navOptions);
                                Toast.makeText(context, "Заявление было успешно отправлено на рассмотрение", Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрыть диалог
                    }
                })
                .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                .show();
        return false;
    }

    private void showConfirmAccept(int statementID, String token_notification, int accountid, String statement_name, String date_start, String date_end, String comment, String full_name)
    {
        Login.playClickSound(requireContext(), "click_tap");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение печати")
                .setMessage(String.format(Locale.US, "Вы уверены, что хотите согласовать заявление №%d?", statementID))
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            if(OnAcceptStatement(statementID, token_notification, accountid, statement_name, date_start, date_end, comment, full_name))
                            {
                                NavController navController = NavHostFragment.findNavController(fragment);
                                Bundle bundle = new Bundle();

                                NavOptions navOptions = new NavOptions.Builder()
                                        .setEnterAnim(R.anim.fade_in)
                                        .setExitAnim(R.anim.slide_out_left)
                                        .setPopEnterAnim(R.anim.fade_in)
                                        .setPopExitAnim(R.anim.fade_out)
                                        .build();

                                navController.navigate(R.id.action_service_additional_to_navigation_services, bundle, navOptions);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрыть диалог
                    }
                })
                .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                .show();
    }

    private void showConfirmCancel(int statementID, String token_notification, String full_name)
    {
        Login.playClickSound(requireContext(), "click_tap");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение печати")
                .setMessage(String.format(Locale.US, "Вы уверены, что хотите отклонить заявление №%d?", statementID))
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            if(OnRejectStatement(statementID, token_notification, full_name))
                            {
                                NavController navController = NavHostFragment.findNavController(fragment);
                                Bundle bundle = new Bundle();

                                NavOptions navOptions = new NavOptions.Builder()
                                        .setEnterAnim(R.anim.fade_in)
                                        .setExitAnim(R.anim.slide_out_left)
                                        .setPopEnterAnim(R.anim.fade_in)
                                        .setPopExitAnim(R.anim.fade_out)
                                        .build();

                                navController.navigate(R.id.action_service_additional_to_navigation_services, bundle, navOptions);
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрыть диалог
                    }
                })
                .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                .show();
    }

    private void showConfirmSendMessage(int send_accountid, int type, List<String> select_employers, String title, String message)
    {
        Login.playClickSound(requireContext(), "click_tap");

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение отправки")
                .setMessage("Вы уверены, что хотите отправить сообщение этим сотрудникам")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        for(int i = 0; i < select_employers.size(); i++)
                        {
                            int accountid = 0;
                            try {
                                accountid = user_data.GetAccountIDOfFullName(select_employers.get(i));

                                try
                                {
                                    if (OnAddMessageEmployer(accountid, send_accountid, type, title, message))
                                    {
                                    }
                                } catch (SQLException e) {
                                    throw new RuntimeException(e);
                                }
                            } catch (SQLException e) {
                                throw new RuntimeException(e);
                            }
                        }

                        NavController navController = NavHostFragment.findNavController(fragment);
                        Bundle bundle = new Bundle();

                        NavOptions navOptions = new NavOptions.Builder()
                                .setEnterAnim(R.anim.fade_in)
                                .setExitAnim(R.anim.slide_out_left)
                                .setPopEnterAnim(R.anim.fade_in)
                                .setPopExitAnim(R.anim.fade_out)
                                .build();

                        navController.navigate(R.id.action_service_additional_to_navigation_services, bundle, navOptions);
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрыть диалог
                    }
                })
                .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                .show();
    }

    private boolean OnAddMessageEmployer(int accountid, int send_accountid, int type, String title, String message) throws SQLException {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO `messages` (`accountid`, `type`, `title`, `message`, `send_accountid`, `send_date`) " +
                            "VALUES ('%d', '%d', '%s', '%s', '%d', CURDATE())", accountid, type, title, message, send_accountid);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                String token_notification = user_data.GetTokenOfAccountID(accountid);
                MainActivity.sendPushNotification(token_notification, title, message);
                return true;
            }
            else return false;
        }
        return false;
    }

    private boolean SendNewLate(int accountid, String date_late, String time_late, String comment)
    {
        if(!IsValidDate(context, date_late))
            return false;

        if(!IsValidTime(context, time_late))
            return false;

        if(!IsValidComment(comment))
            return false;

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение отправки")
                .setMessage("Вы уверены, что хотите отправить новое опоздание?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try {
                            if(OnAddLate(accountid, date_late, time_late, comment))
                            {
                                NavController navController = NavHostFragment.findNavController(fragment);
                                Bundle bundle = new Bundle();

                                NavOptions navOptions = new NavOptions.Builder()
                                        .setEnterAnim(R.anim.fade_in)
                                        .setExitAnim(R.anim.slide_out_left)
                                        .setPopEnterAnim(R.anim.fade_in)
                                        .setPopExitAnim(R.anim.fade_out)
                                        .build();

                                navController.navigate(R.id.action_service_additional_to_navigation_services, bundle, navOptions);
                                Toast.makeText(context, "Опоздание было успешно записано", Toast.LENGTH_SHORT).show();
                            }
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                })
                .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss(); // Закрыть диалог
                    }
                })
                .setCancelable(false) // Запретить закрытие при нажатии вне диалога
                .show();
        return false;
    }

    private boolean OnAddStatement(int accountid, String statement_name, String date_start, String date_end, String comment) throws SQLException {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO statements (accountid, statement_type, date_start, date_end, date_create, `comment`) " +
                    "VALUES (%d, %d, '%s', '%s', CURRENT_DATE(), '%s')",
                    accountid,
                    StatementClass.getStatementIntOfName(statement_name),
                    date_start,
                    date_end,
                    comment);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            if (rowsAffected > 0)
            {
                // Получение сгенерированного ключа (id)
                try (ResultSet generatedKeys = statement.getGeneratedKeys())
                {
                    if (generatedKeys.next())
                    {
                        int statementid = generatedKeys.getInt(1);
                        ServiceArchive.AddStatementLog(statementid, accountid, StatementClass.getStatementIntOfName(statement_name), date_start, date_end, comment, 0);
                        return true;
                    }
                    else throw new SQLException("Не удалось получить id вставленной строки.");
                }
            }
            else return false;
        }
        return false;
    }

    private boolean OnAddLate(int accountid, String date_late, String time_late, String comment) throws SQLException {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO lates (accountid, date, arrival_time, comment) " +
                            "VALUES (%d, '%s', '%s', '%s')",
                    accountid,
                    date_late,
                    time_late,
                    comment);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            if (rowsAffected > 0)
            {
                // Получение сгенерированного ключа (id)
                try (ResultSet generatedKeys = statement.getGeneratedKeys())
                {
                    if (generatedKeys.next())
                    {
                        int lateid = generatedKeys.getInt(1);
                        ServiceArchive.AddLateLog(lateid, accountid, date_late, time_late, comment);
                        return true;
                    }
                    else throw new SQLException("Не удалось получить id вставленной строки.");
                }
            }
            else return false;
        }
        return false;
    }

    private boolean OnReadMessage(int messageid) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "UPDATE `messages` SET `is_read` = 1 WHERE `id` = '%d'", messageid);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                return true;
            }
            else return false;
        }
        return false;
    }

    private boolean OnAcceptStatement(int statementID, String token_notification, int accountid, String statement_name, String date_start, String date_end, String comment, String full_name) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "UPDATE `statements` SET `is_accept` = 1 WHERE `id` = '%d'", statementID);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                MainActivity.sendPushNotification(token_notification, "Уважаемый " + full_name, String.format(Locale.US, "Ваше заявление %d было одобрено", statementID));
                return OnAddHolidaysSQL(statementID, accountid, statement_name, date_start, date_end, comment);
            }
            else return false;
        }
        return false;
    }

    private boolean OnRejectStatement(int statementID, String token_notification, String full_name) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "DELETE FROM `statements` WHERE `id` = '%d'", statementID);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                MainActivity.sendPushNotification(token_notification, "Уважаемый " + full_name, String.format(Locale.US, "Ваше заявление %d было отклонено.", statementID));
                return true;
            }
            else return false;
        }
        return false;
    }

    public boolean OnAddHolidaysSQL(int statementID, int accountid, String statement_name, String date_start, String date_end, String comment) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO holidays (accountid, statementid, begin_holidays, end_holidays, is_own_expense, reason) " +
                            "VALUES (%d, %d, '%s', '%s', %d, '%s')",
                    accountid,
                    statementID,
                    date_start,
                    date_end,
                    StatementClass.isOwnExpense(statement_name),
                    comment);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                return true;
            }
            else return false;
        }
        return false;
    }

    public static boolean IsValidDate(Context context, String date)
    {
        if(date.isEmpty())
        {
            Toast.makeText(context, "Введите дату!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d("debug", date);





        return true;
    }

    public static boolean IsValidTime(Context context, String time)
    {
        if(time.isEmpty())
        {
            Toast.makeText(context, "Введите время!", Toast.LENGTH_SHORT).show();
            return false;
        }

        Log.d("debug", time);

        if(!time.matches("\\d{2}:\\d{2}:\\d{2}"))
        {
            Toast.makeText(context, "Время должно быть формата HH:MM:SS", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean IsValidStatementName(String name)
    {
        if(name.isEmpty())
        {
            Toast.makeText(context, "Выберите тип заявления", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!StatementClass.isValidStatementName(name))
        {
            return false;
        }

        return true;
    }

    private boolean IsValidComment(String comment)
    {
        if(comment.length() > StatementClass.MAX_COMMENT_LENGTH)
        {
            Toast.makeText(context, String.format("Комментарий не должен превышать %d символов", StatementClass.MAX_COMMENT_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean IsValidTitle(String title)
    {
        if(title.isEmpty())
        {
            Toast.makeText(context, "Заполните заголовок!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(title.length() > 50)
        {
            Toast.makeText(context, "Заголовок не должен превышать 50 символов", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean IsValidMessage(String message)
    {
        if(message.isEmpty())
        {
            Toast.makeText(context, "Заполните сообщение!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(message.length() > MessageClass.MAX_COMMENT_LENGTH)
        {
            Toast.makeText(context, String.format(Locale.US, "Заголовок не должен превышать %d символов", MessageClass.MAX_COMMENT_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
