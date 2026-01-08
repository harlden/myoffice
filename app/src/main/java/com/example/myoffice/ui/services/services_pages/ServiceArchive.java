package com.example.myoffice.ui.services.services_pages;

import static com.example.myoffice.ui.services.services_pages.ServiceEmployers.departments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.myoffice.databinding.ServiceArchiveBinding;
import com.example.myoffice.databinding.ServiceEmployersBinding;
import com.example.myoffice.ui.adapters.ActionTypeAdapter;
import com.example.myoffice.ui.adapters.DepartmentAdapter;
import com.example.myoffice.ui.adapters.EmployerActionsAdapter;
import com.example.myoffice.ui.adapters.EmployerAdapter;
import com.example.myoffice.ui.adapters.LateAdapter;
import com.example.myoffice.ui.adapters.LatePersonalAdapter;
import com.example.myoffice.ui.adapters.PersonalStatementAdapter;
import com.example.myoffice.ui.adapters.StatementAdapter;
import com.example.myoffice.ui.services.classes.ActionEmployer;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.classes.LateClass;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;
import com.example.myoffice.user_data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceArchive implements ActionTypeAdapter.OnActionTypeClickListener
{
    public static List<String> actions_names;
    private static List<Employer> employers;
    private static List<StatementClass> statements;
    private static List<ActionEmployer> employers_action;
    private static List<LateClass> lates;
    private List<Employer> originalEmployers;
    private List<StatementClass> originalStatements;
    private List<ActionEmployer> originalActionEmployer;
    private List<LateClass> originalLates;
    private static ActionTypeAdapter actionTypeAdapter;
    private static EmployerAdapter employerAdapter;
    private static StatementAdapter statementAdapter;
    private static EmployerActionsAdapter employerActionsAdapter;
    private static LateAdapter lateAdapter;
    static ResultSet result_query;
    public Context context;
    public RecyclerView action_name_recycler_view, items_recycler_view;
    public ServiceArchiveBinding binding_archive;
    private int select_action_type;
    private EditText search_field;

    public ServiceArchive(Context context)
    {
        this.context = context;
    }

    public boolean LoadItems(ServiceArchiveBinding binding_archive) throws SQLException
    {
        employers = new ArrayList<>();
        statements = new ArrayList<>();
        employers_action = new ArrayList<>();
        lates = new ArrayList<>();

        this.binding_archive = binding_archive;
        this.search_field = binding_archive.searchEditText;

        search_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не используется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызываем метод поиска при изменении текста
                OnFilterItems(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не используется
            }
        });

        return LoadActionNames(0);
    }

    @Override
    public void onActionTypeClick(String action_name, int position) throws SQLException
    {
        Login.playClickSound(context, "click_tap");
        LoadDataBaseItemsActionType(position);

        select_action_type = position;
    }

    private boolean LoadActionNames(int select_action_type) throws SQLException
    {
        String[] array_action_name = {"Профили", "Заявления", "Операции", "Опоздания"};

        action_name_recycler_view = binding_archive.recyclerArchiveType;
        actions_names = Arrays.asList(array_action_name);

        actionTypeAdapter = new ActionTypeAdapter(actions_names, this::onActionTypeClick, select_action_type);

        action_name_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        action_name_recycler_view.setAdapter(actionTypeAdapter);

        LoadDataBaseItemsActionType(select_action_type);
        return true;
    }

    private void LoadDataBaseItemsActionType(int action_type) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String table_name = "";
            String query = "";

            switch (action_type)
            {
                case 0:
                {
                    query = "SELECT * FROM `accounts_log` ORDER BY `id` DESC";
                    break;
                }
                case 1:
                {
                    query = "SELECT t1.statement_id, t1.statement_type, t1.date_start, t1.date_end, t1.comment, t1.date_create, t1.is_accept, t2.surname, t2.name " +
                            "FROM `statements_log` AS t1 LEFT JOIN `accounts` AS t2 ON t1.accountid = t2.id ORDER BY t1.`id` DESC";
                    break;
                }
                case 2:
                {
                    query = "SELECT t1.employes_action_id, t1.accountid, t1.action_type, t1.order, t1.date, t1.comment, t2.name, t2.surname " +
                            "FROM `employes_actions_log` AS t1 LEFT JOIN `accounts` AS t2 ON t1.accountid = t2.id ORDER BY t1.`id` DESC";
                    break;
                }
                case 3:
                {
                    query = "SELECT t1.lateid, t1.accountid, t1.date, t1.arrival_time, t1.comment, t2.name, t2.surname " +
                            "FROM `lates_log` AS t1 LEFT JOIN `accounts` AS t2 ON t1.accountid = t2.id ORDER BY t1.`id` DESC";
                    break;
                }
            }

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            items_recycler_view = binding_archive.recyclerArchiveItems;
            employers.clear();
            statements.clear();
            employers_action.clear();
            lates.clear();

            while (result_query.next())
            {
                switch (action_type)
                {
                    case 0:
                    {
                        Integer accountid = result_query.getInt("id");
                        String name = result_query.getString("name");
                        String surname = result_query.getString("surname");
                        String jobRank = user_data.GetJobTitle(result_query.getInt("job_rank"));
                        String date_invite = result_query.getDate("date_invite").toString();
                        String order_invite = result_query.getString("order_invite");

                        employers.add(new Employer(accountid, name, surname, jobRank, date_invite, order_invite, 5, 2));

                        originalEmployers = new ArrayList<>(employers);

                        LoadCardsEmployers(employers);
                        break;
                    }
                    case 1:
                    {
                        Integer statementID = result_query.getInt("statement_id");
                        Integer statementType = result_query.getInt("statement_type");
                        Date dateStart = result_query.getDate("date_start");
                        Date dateEnd = result_query.getDate("date_end");
                        Date dateCreate = result_query.getDate("date_create");
                        String comment = result_query.getString("comment");
                        Integer is_accept = result_query.getInt("is_accept");
                        String surname = result_query.getString("surname");
                        String name = result_query.getString("name");

                        statements.add(new StatementClass(statementID, statementType, surname + " " + name, dateStart, dateEnd,
                                comment, dateCreate, is_accept, ""));

                        originalStatements = new ArrayList<>(statements);
                        LoadCardsStatements(statements);
                        break;
                    }
                    case 2:
                    {
                        Integer actionid = result_query.getInt("employes_action_id");
                        Integer accountid = result_query.getInt("accountid");
                        Integer employes_action_type = result_query.getInt("action_type");
                        String name = result_query.getString("name");
                        String surname = result_query.getString("surname");
                        String date = result_query.getDate("date").toString();
                        String order = result_query.getString("order");
                        String comment = result_query.getString("comment");

                        employers_action.add(new ActionEmployer(actionid, accountid, employes_action_type, order, date, name, surname, comment));

                        originalActionEmployer = new ArrayList<>(employers_action);

                        LoadCardsEmployersAction(employers_action);
                        break;
                    }
                    case 3:
                    {
                        String surname = result_query.getString("surname");
                        String name = result_query.getString("name");
                        String comment = result_query.getString("comment");
                        Date date = result_query.getDate("date");
                        int lateID = result_query.getInt("lateid");
                        int accountid = result_query.getInt("accountid");
                        Time arrival_time = result_query.getTime("arrival_time");

                        lates.add(new LateClass(lateID, accountid, surname + ' ' + name, comment, date, arrival_time));

                        originalLates = new ArrayList<>(lates);

                        LoadCardsLates(lates);
                        break;
                    }
                }
            }
        }
    }

    private void OnFilterItems(String search_string)
    {
        List<Employer> filteredList = new ArrayList<>();
        List<StatementClass> filteresListStatements = new ArrayList<>();
        List<ActionEmployer> filterListActionEmployers = new ArrayList<>();
        List<LateClass> filterListLates = new ArrayList<>();

        switch (select_action_type)
        {
            case 0: {
                if (!search_string.isEmpty()) {
                    for (Employer employer : originalEmployers)
                    {
                        if (IsFindFilterEmployer(employer, search_string)) {
                            filteredList.add(employer);
                        }
                    }
                } else {
                    filteredList.addAll(originalEmployers);
                }
                employerAdapter.updateList(filteredList);
                break;
            }
            case 1:
            {
                if (!search_string.isEmpty()) {
                    for (StatementClass statementClass : originalStatements)
                    {
                        if (IsFindFilterStatement(statementClass, search_string)) {
                            filteresListStatements.add(statementClass);
                        }
                    }
                } else {
                    filteresListStatements.addAll(originalStatements);
                }
                statementAdapter.updateList(filteresListStatements);
            }
            case 2:
            {
                if (!search_string.isEmpty()) {
                    for (ActionEmployer employer : originalActionEmployer)
                    {
                        if (IsFindFilterActionEmployer(employer, search_string)) {
                            filterListActionEmployers.add(employer);
                        }
                    }
                } else {
                    filterListActionEmployers.addAll(originalActionEmployer);
                }
                employerActionsAdapter.updateList(filterListActionEmployers);
                break;
            }
            case 3:
            {
                if (!search_string.isEmpty()) {
                    for (LateClass lateClass : originalLates)
                    {
                        if (IsFindFilterLates(lateClass, search_string)) {
                            filterListLates.add(lateClass);
                        }
                    }
                } else {
                    filterListLates.addAll(originalLates);
                }
                lateAdapter.updateList(filterListLates);
                break;
            }
        }
    }

    private void LoadCardsStatements(List<StatementClass> statements)
    {
        statementAdapter = new StatementAdapter(statements, true, new StatementAdapter.OnStatementClickListener() {
            @Override
            public void onViewClick(StatementClass statement) {

            }

            @Override
            public void onPrintClick(StatementClass statement) {

            }
        });

        items_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        items_recycler_view.setAdapter(statementAdapter);
    }

    private void LoadCardsEmployers(List<Employer> employers)
    {
        employerAdapter = new EmployerAdapter(employers, true, new EmployerAdapter.OnEmployerClickListener() {
            @Override
            public void onEditClick(Employer employer) {

            }
            @Override
            public void onDeleteClick(Employer employer) {

            }
        });

        items_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        items_recycler_view.setAdapter(employerAdapter);
    }

    private void LoadCardsEmployersAction(List<ActionEmployer> employers)
    {
        employerActionsAdapter = new EmployerActionsAdapter(employers, true, new EmployerActionsAdapter.OnEmployerActionClickListener() {
            @Override
            public void onEditClick(ActionEmployer employer) {

            }

            @Override
            public void onDeleteClick(ActionEmployer employer) {

            }
        });

        items_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        items_recycler_view.setAdapter(employerActionsAdapter);
    }

    private void LoadCardsLates(List<LateClass> lates)
    {
        lateAdapter = new LateAdapter(lates);

        items_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        items_recycler_view.setAdapter(lateAdapter);
    }

    public static void AddAccountLog(int accountid, String login, String password, String name, String surname, int job_rank, int department, String date_invite,
                                     String order_invite, String token_notification) throws SQLException {
        if (DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO `accounts_log` (`accountid`, `surname`, `name`, `job_rank`, " +
                            "`department`, `login`, `password`, `date_invite`, `order_invite`, `token_notification`) " +
                            "VALUES ('%d', '%s', '%s', '%d', '%d', '%s', '%s', '%s', `%s`, `'%s')", accountid,
                    surname, name, job_rank, department + 1, login, password, date_invite, order_invite, token_notification);

            Statement statement = DataBase.connection.createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        }
    }
    public static void AddStatementLog(int statementid, int accountid, int statement_type, String date_start, String date_end, String comment, int is_accept) throws SQLException {
        if (DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO statements_log (statement_id, accountid, statement_type, date_start, date_end, date_create, `comment`, is_accept) " +
                            "VALUES (%d, %d, %d, '%s', '%s', CURDATE(), '%s', '%d')",
                    statementid, accountid, statement_type, date_start, date_end, comment, is_accept);

            Statement statement = DataBase.connection.createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        }
    }
    public static void AddEmployesActionLog(int actionid, int accountid, int action_type, String order, String date, String comment) throws SQLException {
        if (DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO `employes_actions_log` (`employes_action_id`, `accountid`, `action_type`, " +
                            "`order`, `date`, `comment`) VALUES ('%d', '%d', '%d', '%s', '%s', '%s')",
                    actionid, accountid, action_type, order, date, comment);

            Statement statement = DataBase.connection.createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        }
    }
    public static void AddLateLog(int lateid, int accountid, String date_late, String time_late, String comment) throws SQLException {
        if (DataBase.connection != null)
        {
            String query = String.format(Locale.US, "INSERT INTO lates_log (lateid, accountid, date, arrival_time, comment) " +
                            "VALUES (%d, %d, '%s', '%s', '%s')",
                    lateid,
                    accountid,
                    date_late,
                    time_late,
                    comment);

            Statement statement = DataBase.connection.createStatement();
            statement.executeUpdate(query, Statement.RETURN_GENERATED_KEYS);
        }
    }
    private boolean IsFindFilterEmployer(Employer employer, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return employer.getName().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getSurname().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getJobTitle().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(employer.getAccountID()).equals(search_string);
    }
    private boolean IsFindFilterActionEmployer(ActionEmployer employer, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return employer.getName().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getDate().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getComment().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(employer.getAccountID()).equals(search_string);
    }
    private boolean IsFindFilterStatement(StatementClass statementClass, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return statementClass.getFullName().toLowerCase().contains(lowerCaseSearchString) ||
                statementClass.getComment().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(statementClass.getStatementID()).equals(search_string);
    }
    private boolean IsFindFilterLates(LateClass statementClass, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return statementClass.getFullName().toLowerCase().contains(lowerCaseSearchString) ||
                statementClass.getComment().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(statementClass.getLateID()).equals(search_string);
    }
}
