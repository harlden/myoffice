package com.example.myoffice.ui.services.services_pages;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.EditTextDialog;
import com.example.myoffice.EditTextItem;
import com.example.myoffice.Login;
import com.example.myoffice.databinding.ServiceEmployersActionsBinding;
import com.example.myoffice.databinding.ServiceEmployersBinding;
import com.example.myoffice.ui.adapters.ActionTypeAdapter;
import com.example.myoffice.ui.adapters.DepartmentAdapter;
import com.example.myoffice.ui.adapters.EmployerActionsAdapter;
import com.example.myoffice.ui.adapters.EmployerAdapter;
import com.example.myoffice.ui.services.classes.ActionEmployer;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;
import com.example.myoffice.user_data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class ServiceActionsEmployers
{
    public static List<String> actions_names;
    private static List<ActionEmployer> employers;
    private List<ActionEmployer> originalEmployers;
    public static ActionTypeAdapter actionTypeAdapter;
    public static EmployerActionsAdapter employerActionsAdapter;
    static ResultSet result_query;
    public Context context;
    public RecyclerView action_name_recycler_view, employers_actions_recycler_view;
    public ServiceEmployersActionsBinding binding_employers_actions;
    private EditTextItem[] items;
    private EditText search_field;
    private ImageButton add_employer_action_button;

    public ServiceActionsEmployers(Context context)
    {
        this.context = context;
    }

    public boolean LoadEmployers(ServiceEmployersActionsBinding binding_employers_actions) throws SQLException
    {
        employers = new ArrayList<>();

        this.binding_employers_actions = binding_employers_actions;
        this.search_field = binding_employers_actions.searchEditText;
        this.add_employer_action_button = binding_employers_actions.addEmployerActionButton;

        search_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не используется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызываем метод поиска при изменении текста
                OnFilterEmployers(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не используется
            }
        });

        add_employer_action_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Login.playClickSound(context, "click_tap");

                List<String> employers_all = new ArrayList<>();
                try
                {
                    user_data.LoadAllEmployers(employers_all, false);

                    items = new EditTextItem[]{
                            new EditTextItem("Сотрудник", "", employers_all, false, 0, false),
                            new EditTextItem("Тип действия", "", List.of(ActionEmployer.actions_employers_name), false, 0, false),
                            new EditTextItem("Дата приказа", "YYYY-MM-DD", "", false, true),
                            new EditTextItem("№ приказа", "", "", false, false),
                            new EditTextItem("Комментарий", "", "", false, false)
                    };

                    // Создаем и показываем диалог
                    EditTextDialog editTextDialog = new EditTextDialog(context, items, this::onDataSubmit);
                    editTextDialog.show();
                }
                catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }

            private boolean onDataSubmit(List<String> strings) throws SQLException
            {
                return OnProcessEditTextDialog(strings, true);
            }
        });

        return LoadActionNames(0);
    }

    public void OnActionTypeClick(String action_name, int select_action_name) throws SQLException
    {
        Login.playClickSound(context, "click_tap");
        LoadDataBaseEmployersActionType(select_action_name);
    }

    private boolean LoadActionNames(int select_action_type) throws SQLException
    {
        action_name_recycler_view = binding_employers_actions.recyclerActionType;
        actions_names = Arrays.asList(ActionEmployer.actions_employers_name);

        actionTypeAdapter = new ActionTypeAdapter(actions_names, this::OnActionTypeClick, select_action_type);

        action_name_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        action_name_recycler_view.setAdapter(actionTypeAdapter);

        LoadDataBaseEmployersActionType(select_action_type);
        return true;
    }

    private void LoadDataBaseEmployersActionType(int action_type) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "SELECT t1.id, t1.accountid, t1.action_type, t1.order, t1.date, t1.comment, " +
                    "t2.name, t2.surname FROM `employes_actions` AS t1 LEFT JOIN `accounts` AS t2 ON t1.accountid = " +
                    "t2.id WHERE t1.action_type = '%d'", action_type);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            employers_actions_recycler_view = binding_employers_actions.recyclerEmployersActions;
            employers.clear();

            while (result_query.next())
            {
                Integer actionid = result_query.getInt("id");
                Integer accountid = result_query.getInt("accountid");
                String name = result_query.getString("name");
                String surname = result_query.getString("surname");
                String date = result_query.getDate("date").toString();
                String order = result_query.getString("order");
                String comment = result_query.getString("comment");

                employers.add(new ActionEmployer(actionid, accountid, action_type, order, date, name, surname, comment));

            }

            originalEmployers = new ArrayList<>(employers);
            LoadCardsEmployers(employers);
        }
    }

    private void OnFilterEmployers(String search_string) {
        List<ActionEmployer> filteredList = new ArrayList<>();

        if (!search_string.isEmpty()) {
            for (ActionEmployer employer : originalEmployers)
            {
                if (IsFindFilter(employer, search_string)) {
                    filteredList.add(employer);
                }
            }
        } else {
            filteredList.addAll(originalEmployers);
        }
        // Обновляем адаптер
        employerActionsAdapter.updateList(filteredList);
    }

    private void LoadCardsEmployers(List<ActionEmployer> employers)
    {
        employerActionsAdapter = new EmployerActionsAdapter(employers, false, new EmployerActionsAdapter.OnEmployerActionClickListener()
        {
            @Override
            public void onEditClick(ActionEmployer employer)
            {
                Login.playClickSound(context, "click_tap");

                items = new EditTextItem[]
                {
                        new EditTextItem("UID", "", String.valueOf(employer.getActionID()), true, false),
                        new EditTextItem("Сотрудник", "", String.valueOf(employer.getFullName()), true, false),
                        new EditTextItem("Дата приказа", "YYYY-MM-DD", employer.getDate(), false, true),
                        new EditTextItem("№ приказа", "", employer.getOrder(), false, false),
                        new EditTextItem("Комментарий", "", employer.getComment(), false, false)
                };

                EditTextDialog editTextDialog = new EditTextDialog(context, items, this::onDataSubmit);
                editTextDialog.show();
            }

            private boolean onDataSubmit(List<String> strings) throws SQLException {
                return OnProcessEditTextDialog(strings, false);
            }

            @Override
            public void onDeleteClick(ActionEmployer employer) {
                Login.playClickSound(context, "click_tap");
                showDeleteConfirmationDialog(employer.getActionID());
            }
        });

        employers_actions_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        employers_actions_recycler_view.setAdapter(employerActionsAdapter);
    }

    private void showDeleteConfirmationDialog(int actionid)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение удаления")
                .setMessage(String.format(Locale.US, "Вы уверены, что хотите удалить действие с UID: %d?", actionid))
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            if (DeleteAction(actionid))
                            {
                                employers.removeIf(employer -> employer.getActionID() == actionid);
                                employerActionsAdapter.notifyDataSetChanged();

                                Toast.makeText(context, String.format(Locale.US, "Вы успешно удалили действие с UID: %d", actionid), Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(context, "Действие не получилось удалить", Toast.LENGTH_SHORT).show();
                        }
                        catch (SQLException e)
                        {
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

    private boolean DeleteAction(int actionid) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "DELETE FROM `employes_actions` WHERE `id` = '%d'", actionid);

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

    public static boolean SaveAction(int actionid, int accountid, int action_type, String date, String order, String comment) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "UPDATE `employes_actions` SET `accountid` = '%d', " +
                            "action_type = '%d'," +
                            "order = '%s', " +
                            "date = '%s', " +
                            "comment = '%s', " +
                            "WHERE `id` = '%d'",
                            accountid,
                            action_type,
                            order,
                            date,
                            comment,
                            actionid);

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

    private int AddAction(int accountid, int action_type, String order, String date, String comment) throws SQLException {
        if (DataBase.connection != null)
        {
            // Подготовка SQL-запроса
            String query = String.format(Locale.US, "INSERT INTO `employes_actions` (`accountid`, `action_type`, " +
                            "`order`, `date`, `comment`) VALUES ('%d', '%d', '%s', '%s', '%s')",
                    accountid, action_type, order, date, comment);

            Statement statement = DataBase.connection.createStatement();

            // Выполнение запроса и получение количества затронутых строк
            int rowsAffected = statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);

            if (rowsAffected > 0)
            {
                // Получение сгенерированного ключа (id)
                try (ResultSet generatedKeys = statement.getGeneratedKeys())
                {
                    if (generatedKeys.next())
                    {
                        int actionid = generatedKeys.getInt(1);
                        ServiceArchive.AddEmployesActionLog(actionid, accountid, action_type, order, date, comment);
                        return actionid; // Возвращаем id вставленной строки
                    }
                    else throw new SQLException("Не удалось получить id вставленной строки.");
                }
            }
        }
        return -1;
    }

    private boolean OnProcessEditTextDialog(List<String> strings, boolean is_add) throws SQLException
    {
        Log.d("DEBUG", strings.toString());
        if (items == null || strings == null) {
            throw new IllegalArgumentException("Items array or strings list is null");
        }

        if (items.length != strings.size()) {
            throw new IllegalArgumentException("Items array size does not match strings list size");
        }

        String date = "", order = "", comment = "";
        int actionid = -1, accountid = -1,  action_type = -1;

        for (int i = 0; i < strings.size(); i++)
        {
            String input = strings.get(i);
            String hint = items[i].getDescription();

            switch (hint)
            {
                case "UID":
                    if(!is_add)
                    {
                        actionid = Integer.parseInt(input);
                    }
                    break;
                case "Тип действия":
                    if(is_add)
                    {
                        action_type = ActionEmployer.getActionType(input);
                    }
                    break;
                case "Сотрудник":
                    if(is_add && user_data.GetAccountIDOfFullName(input) != -1)
                    {
                        accountid = user_data.GetAccountIDOfFullName(input);
                    }
                    else return false;
                    break;

                case "Дата приказа":
                    if(ServiceAdditional.IsValidDate(context, input))
                    {
                        date = input;
                    }
                    else return false;
                    break;

                case "№ приказа":
                    if(ServiceEmployers.IsValidOrderInvite(context, input))
                    {
                        order = input;
                    }
                    else return false;
                    break;

                case "Комментарий":
                    if(IsValidComment(context, input))
                    {
                        comment = input;
                    }
                    else return false;

                default:
                    break;
            }
        }

        if(!is_add && actionid != -1 && accountid != -1 && !date.isEmpty() && !order.isEmpty())
        {
            if(SaveAction(actionid, accountid, action_type, date, order, comment))
            {
                for (int i = 0; i < employers.size(); i++)
                {
                    if (employers.get(i).getAccountID() == accountid)
                    {
                        // Обновляем элемент
                        employers.set(i, new ActionEmployer(actionid, accountid, action_type, order, date, employers.get(i).getSurnameEmployer(), employers.get(i).getNameEmployer(), comment));
                        employerActionsAdapter.notifyItemChanged(i);
                        break;
                    }
                }

                Toast.makeText(context, String.format(Locale.US, "Вы изменили информацию об действий UID: %d", actionid), Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        if (is_add && accountid != -1 && !date.isEmpty() && !order.isEmpty() && action_type != -1)
        {
            actionid = AddAction(accountid, action_type, order, date, comment);

            if (actionid != -1)
            {
                String full_name = user_data.GetFullNameOfAccountID(accountid);
                String[] parts = full_name.split(" ");

                employers.add(new ActionEmployer(actionid, accountid, action_type, order, date, parts[1], parts[0], comment));

                if(actionTypeAdapter.selectedPosition == action_type)
                    employerActionsAdapter.notifyItemInserted(employers.size() - 1);

                // Показать сообщение пользователю
                Toast.makeText(context, String.format(Locale.US, "Вы добавили новое действие. (UID: %d)", actionid), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(context, "Ошибка при добавлении действия.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    private boolean IsFindFilter(ActionEmployer employer, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return employer.getName().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getFullName().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(employer.getActionID()).equals(search_string);
    }

    private boolean IsValidComment(Context context, String comment)
    {
        if(!comment.isEmpty() && comment.length() > 255)
        {
            Toast.makeText(context, String.format(Locale.US, "Комментарии не должны превышать 255 символов"), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }
}
