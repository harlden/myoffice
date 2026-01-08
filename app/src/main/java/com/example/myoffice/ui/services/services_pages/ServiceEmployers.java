package com.example.myoffice.ui.services.services_pages;

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
import com.example.myoffice.databinding.ServiceEmployersBinding;
import com.example.myoffice.ui.adapters.DepartmentAdapter;
import com.example.myoffice.ui.adapters.EmployerAdapter;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;
import com.example.myoffice.user_data;

import org.apache.poi.hssf.record.PageBreakRecord;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceEmployers implements DepartmentAdapter.OnDepartmentClickListener
{
    public static List<String> departments;
    private static List<Employer> employers;
    private List<Employer> originalEmployers;
    public static DepartmentAdapter departmentAdapter;
    public static EmployerAdapter employerAdapter;
    static ResultSet result_query;
    public Context context;
    public RecyclerView departments_recycler_view, employers_recycler_view;
    public ServiceEmployersBinding binding_employers;
    private EditTextItem[] items;
    private EditText search_field;
    private ImageButton add_employee_button;

    public ServiceEmployers(Context context)
    {
        this.context = context;
    }

    public boolean LoadEmployers(ServiceEmployersBinding binding_employers) throws SQLException
    {
        employers = new ArrayList<>();

        this.binding_employers = binding_employers;
        this.search_field = binding_employers.searchEditText;
        this.add_employee_button = binding_employers.addEmployeeButton;

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

        add_employee_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Login.playClickSound(context, "click_tap");
                items = new EditTextItem[]{
                        new EditTextItem("Фамилия", "", "", false, false),
                        new EditTextItem("Имя", "", "", false, false),
                        new EditTextItem("Должность", "", List.of(user_data.job_name), false, 0, false),
                        new EditTextItem("Цех", "", departments, false, 0, false),
                        new EditTextItem("Логин", "", "", false, false),
                        new EditTextItem("Пароль", "", "", false, false),
                        new EditTextItem("Дата принятия", "YYYY-MM-DD", "", false, true),
                        new EditTextItem("№ приказа принятия", "", "", false, false),
                        new EditTextItem("График работы (день)", "", "5", false, false),
                        new EditTextItem("График работы (через)", "", "2", false, false)
                };

                // Создаем и показываем диалог
                EditTextDialog editTextDialog = new EditTextDialog(context, items, this::onDataSubmit);
                editTextDialog.show();
            }

            private boolean onDataSubmit(List<String> strings) throws SQLException
            {
                return OnProcessEditTextDialog(strings, true);
            }
        });

        return LoadDepartments(0);
    }

    @Override
    public void onDepartmentClick(String department, int position) throws SQLException
    {
        Login.playClickSound(context, "click_tap");
        LoadDataBaseEmployersDepartment(position + 1);
    }

    private boolean LoadDepartments(int select_department) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = "SELECT * FROM `departments`";

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            departments_recycler_view = binding_employers.recyclerView;
            departments = new ArrayList<>();

            while (result_query.next())
            {
                departments.add(result_query.getString("name"));
            }

            departmentAdapter = new DepartmentAdapter(departments, this, select_department);

            departments_recycler_view.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            departments_recycler_view.setAdapter(departmentAdapter);

            LoadDataBaseEmployersDepartment(select_department + 1);
            return true;
        }
        return false;
    }

    private void LoadDataBaseEmployersDepartment(int department) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "SELECT t1.id, t1.name, t1.surname, t1.job_rank, t1.date_invite, t1.order_invite, t1.schedule_work_day_first, " +
                    "t1.schedule_work_day_second FROM " +
                    "`accounts` AS t1 LEFT JOIN `departments` AS t2 ON t1.department = t2.id WHERE `department` = '%d'", department);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            employers_recycler_view = binding_employers.recyclerEmployers;
            employers.clear();

            while (result_query.next())
            {
                Integer accountid = result_query.getInt("id");
                String name = result_query.getString("name");
                String surname = result_query.getString("surname");
                String jobRank = user_data.GetJobTitle(result_query.getInt("job_rank"));
                String date_invite = result_query.getDate("date_invite").toString();
                String order_invite = result_query.getString("order_invite");
                Integer schedule_work_day_first = result_query.getInt("schedule_work_day_first");
                Integer schedule_work_day_second = result_query.getInt("schedule_work_day_second");

                employers.add(new Employer(accountid, name, surname, jobRank, date_invite, order_invite, schedule_work_day_first, schedule_work_day_second));

            }

            originalEmployers = new ArrayList<>(employers);
            LoadCardsEmployers(employers);
        }
    }

    private void OnFilterEmployers(String search_string) {
        List<Employer> filteredList = new ArrayList<>();

        if (!search_string.isEmpty()) {
            for (Employer employer : originalEmployers)
            {
                if (IsFindFilter(employer, search_string)) {
                    filteredList.add(employer);
                }
            }
        } else {
            filteredList.addAll(originalEmployers);
        }
        // Обновляем адаптер
        employerAdapter.updateList(filteredList);
    }

    private void LoadCardsEmployers(List<Employer> employers)
    {
        employerAdapter = new EmployerAdapter(employers, false, new EmployerAdapter.OnEmployerClickListener()
        {
            @Override
            public void onEditClick(Employer employer)
            {
                Login.playClickSound(context, "click_tap");
                items = new EditTextItem[]
                {
                        new EditTextItem("UID", "", String.valueOf(employer.getAccountID()), true, false),
                        new EditTextItem("Фамилия", "", employer.getSurname(), false, false),
                        new EditTextItem("Имя", "", employer.getName(), false, false),
                        new EditTextItem("Должность", "", List.of(user_data.job_name), false, user_data.GetJobRank(employer.getJobTitle()), false),
                        new EditTextItem("Дата принятия", "YYYY-MM-DD", employer.getDateInvite(), false, true),
                        new EditTextItem("№ приказа принятия", "", employer.getOrderInvite(), false, false),
                        new EditTextItem("График работы (день)", "", employer.getScheduleWorkDayFirst().toString(), false, false),
                        new EditTextItem("График работы (через)", "", employer.getScheduleWorkDaySecond().toString(), false, false)
                };

                EditTextDialog editTextDialog = new EditTextDialog(context, items, this::onDataSubmit);
                editTextDialog.show();
            }

            private boolean onDataSubmit(List<String> strings) throws SQLException {
                return OnProcessEditTextDialog(strings, false);
            }

            @Override
            public void onDeleteClick(Employer employer) {
                Login.playClickSound(context, "click_tap");
                showDeleteConfirmationDialog(employer.getAccountID());
            }
        });

        employers_recycler_view.setLayoutManager(new LinearLayoutManager(context));
        employers_recycler_view.setAdapter(employerAdapter);
    }

    private void showDeleteConfirmationDialog(int accountid)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Подтверждение удаления")
                .setMessage(String.format(Locale.US, "Вы уверены, что хотите удалить аккаунт с UID: %d?", accountid))
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        try
                        {
                            if (DeleteAccount(accountid))
                            {
                                employers.removeIf(employer -> employer.getAccountID() == accountid);
                                employerAdapter.notifyDataSetChanged();

                                Toast.makeText(context, String.format(Locale.US, "Вы успешно удалили аккаунт с UID: %d", accountid), Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(context, "Аккаунт не получилось удалить", Toast.LENGTH_SHORT).show();
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

    private boolean DeleteAccount(int accountid) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "DELETE FROM `accounts` WHERE `id` = '%d'", accountid);

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

    public static boolean SaveAccount(int accountid, String name, String surname, int job_rank, String date_invite, String order_invite, Integer schedule_work_day_first,
                                      Integer schedule_work_day_second) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "UPDATE `accounts` SET `name` = '%s', " +
                            "surname = '%s'," +
                            "job_rank = '%d', " +
                            "date_invite = '%s', " +
                            "order_invite = '%s', " +
                            "schedule_work_day_first = '%d', " +
                            "schedule_work_day_second = '%d' " +
                            "WHERE `id` = '%d'",
                            name,
                            surname,
                            job_rank,
                            date_invite,
                            order_invite,
                            schedule_work_day_first,
                            schedule_work_day_second,
                            accountid);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if(accountid == (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID))
            {
                user_data.SetUserData(user_data.E_USER_DATA.DATA_NAME, name);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_SURNAME, surname);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_JOB_RANK, job_rank);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_DATE_INVITE, date_invite);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_ORDER_INVITE, order_invite);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_SCHEDULE_WORK_DAY_FIRST, schedule_work_day_first);
                user_data.SetUserData(user_data.E_USER_DATA.DATA_SCHEDULE_WORK_DAY_SECOND, schedule_work_day_second);
            }

            if (rowsAffected > 0)
            {
                return true;
            }
            else return false;
        }
        return false;
    }

    private int AddAccount(String surname, String name, int job_rank, String department, String login, String password, String date_invite, String order_invite,
                           Integer schedule_work_day_first, Integer schedule_work_day_second) throws SQLException {
        if (DataBase.connection != null)
        {
            int department_id = departments.indexOf(department);

            // Подготовка SQL-запроса
            String query = String.format(Locale.US, "INSERT INTO `accounts` (`surname`, `name`, `job_rank`, " +
                            "`department`, `login`, `password`, `date_invite`, `order_invite`, `schedule_work_day_first`, `schedule_work_day_second`) " +
                            "VALUES ('%s', '%s', '%d', '%d', '%s', '%s', '%s', '%s', '%d', '%d')",
                    surname, name, job_rank, department_id + 1, login, password, date_invite, order_invite, schedule_work_day_first, schedule_work_day_second);

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
                        int accountid = generatedKeys.getInt(1);
                        ServiceArchive.AddAccountLog(accountid, login, password, name, surname, job_rank, department_id + 1, date_invite, order_invite, "");
                        return accountid; // Возвращаем id вставленной строки
                    }
                    else throw new SQLException("Не удалось получить id вставленной строки.");
                }
            }
        }
        return -1;
    }

    private boolean OnProcessEditTextDialog(List<String> strings, boolean is_add) throws SQLException
    {
        if (items == null || strings == null) {
            throw new IllegalArgumentException("Items array or strings list is null");
        }

        if (items.length != strings.size()) {
            throw new IllegalArgumentException("Items array size does not match strings list size");
        }

        String surname = "", name = "", department = "", login = "", password = "", date_invite = "", order_invite = "";
        int accountid = -1, job_rank = -1, schedule_work_day_first = -1, schedule_work_day_second = -1;

        for (int i = 0; i < strings.size(); i++)
        {
            String input = strings.get(i);
            String hint = items[i].getDescription();

            switch (hint)
            {
                case "UID":
                    if(!is_add)
                    {
                        accountid = Integer.parseInt(input);
                    }
                    break;
                case "Фамилия":
                    if(IsValidSurname(context, input))
                    {
                        surname = input;
                    }
                    else return false;
                    break;
                case "Имя":
                    if(IsValidName(context, input)){
                        name = input;
                    }
                    else return false;
                    break;

                case "Должность":
                    if(IsValidJobTitle(context, input))
                    {
                        job_rank = user_data.GetJobRank(input);
                    }
                    else return false;
                    break;

                case "Цех":
                    if(is_add)
                    {
                        if(IsValidDepartment(input))
                        {
                            department = input;
                        }
                        else return false;
                    }
                    break;

                case "Логин":
                    if(is_add)
                    {
                        if(IsValidLogin(input))
                        {
                            login = input;
                        }
                        else return false;
                    }
                    break;

                case "Пароль":
                    if(is_add)
                    {
                        if(IsValidPassword(input))
                        {
                            password = input;
                        }
                        else return false;
                    }
                    break;

                case "Дата принятия":
                    if(ServiceAdditional.IsValidDate(context, input))
                    {
                        date_invite = input;
                    }
                    else return false;
                    break;

                case "№ приказа принятия":
                    if(IsValidOrderInvite(context, input))
                    {
                        order_invite = input;
                    }
                    else return false;
                    break;

                case "График работы (день)":
                    if(IsValidScheduleWorkDay(context, input))
                    {
                        schedule_work_day_first = Integer.parseInt(input);
                    }
                    else return false;
                    break;

                case "График работы (через)":
                    if(IsValidScheduleWorkDay(context, input))
                    {
                        schedule_work_day_second = Integer.parseInt(input);
                    }
                    else return false;
                    break;

                default:
                    break;
            }
        }

        if(!is_add && accountid != -1 && !surname.isEmpty() && !name.isEmpty() && job_rank != -1 && !date_invite.isEmpty() && !order_invite.isEmpty() &&
                schedule_work_day_first != -1 && schedule_work_day_second != -1)
        {
            if(SaveAccount(accountid, name, surname, job_rank, date_invite, order_invite, schedule_work_day_first, schedule_work_day_second))
            {
                for (int i = 0; i < employers.size(); i++)
                {
                    if (employers.get(i).getAccountID() == accountid)
                    {
                        // Обновляем элемент
                        employers.set(i, new Employer(accountid, name, surname, user_data.GetJobTitle(job_rank), date_invite, order_invite, schedule_work_day_first, schedule_work_day_second));
                        employerAdapter.notifyItemChanged(i);
                        break;
                    }
                }

                Toast.makeText(context, String.format(Locale.US, "Вы изменили информацию об аккаунте UID: %d", accountid), Toast.LENGTH_SHORT).show();
                return true;
            }
        }

        if (is_add && !surname.isEmpty() && !name.isEmpty() && job_rank != -1 && !department.isEmpty() && !login.isEmpty() && !password.isEmpty() &&
            !date_invite.isEmpty() && !order_invite.isEmpty() && schedule_work_day_first != -1 && schedule_work_day_second != -1)
        {
            accountid = AddAccount(surname, name, job_rank, department, login, password, date_invite, order_invite, schedule_work_day_first, schedule_work_day_second);

            if (accountid != -1)
            {
                int department_position = departments.indexOf(department);
                employers.add(new Employer(accountid, surname, name, user_data.GetJobTitle(job_rank), date_invite, order_invite, schedule_work_day_first, schedule_work_day_second));

                if(departmentAdapter.selectedPosition == department_position)
                    employerAdapter.notifyItemInserted(employers.size() - 1);

                // Показать сообщение пользователю
                Toast.makeText(context, String.format(Locale.US, "Вы добавили новый аккаунт. %s %s (UID: %d)", surname, name, accountid), Toast.LENGTH_SHORT).show();
                return true;
            } else {
                Toast.makeText(context, "Ошибка при добавлении аккаунта.", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }

    public static boolean IsValidSurname(Context context, String surname)
    {
        if(surname.isEmpty())
        {
            Toast.makeText(context, "Введите фамилию", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(surname.length() > user_data.MAX_SURNAME_LENGTH)
        {
            Toast.makeText(context, String.format(Locale.US, "Фамилия не должна превышать %d символов", user_data.MAX_SURNAME_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(surname.matches(".*\\d.*"))
        {
            Toast.makeText(context, "Фамилия не должна содержать цифры", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(surname.matches(".*[?#%&!@$^*()]+.*"))
        {
            Toast.makeText(context, "Фамилия содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean IsValidOrderInvite(Context context, String order_invite)
    {
        if(order_invite.isEmpty())
        {
            Toast.makeText(context, "Введите номер приказа", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(order_invite.matches(".*[?#%&!@$^*()]+.*"))
        {
            Toast.makeText(context, "Номер приказа содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean IsValidScheduleWorkDay(Context context, String work_day)
    {
        if(work_day.isEmpty())
        {
            Toast.makeText(context, "Введите день графика работы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!work_day.matches(".*\\d.*"))
        {
            Toast.makeText(context, "День графика работы должен быть в виде числа", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!(Integer.parseInt(work_day) <= ServiceScheduleWork.MAX_SCHEDULE_WORK_DAY && Integer.parseInt(work_day) >= ServiceScheduleWork.MIN_SCHEDULE_WORK_DAY))
        {
            Toast.makeText(context, "День должен быть от 1 до 7", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean IsValidName(Context context, String name)
    {
        if(name.isEmpty())
        {
            Toast.makeText(context, "Введите имя", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.length() > user_data.MAX_NAME_LENGTH)
        {
            Toast.makeText(context, String.format(Locale.US, "Имя не должно превышать %d символов", user_data.MAX_NAME_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.matches(".*\\d.*"))
        {
            Toast.makeText(context, "Имя не должно содержать цифры", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(name.matches(".*[?#%&!@$^*()]+.*"))
        {
            Toast.makeText(context, "Имя содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public static boolean IsValidJobTitle(Context context, String job_title)
    {
        if(job_title.isEmpty())
        {
            Toast.makeText(context, "Введите должность", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(job_title.matches(".*\\d.*"))
        {
            Toast.makeText(context, "Должность не должна содержать цифры", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(job_title.matches(".*[?#%&!@$^*()/]+."))
        {
            Toast.makeText(context, "Должность содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!user_data.IsExistsJobRank(job_title))
        {
            Toast.makeText(context, "Такой должности не существует", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean IsValidDepartment(String department)
    {
        if(department.isEmpty())
        {
            Toast.makeText(context, "Введите цех", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(department.matches(".*\\d.*"))
        {
            Toast.makeText(context, "Цех не должно содержать цифры", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(department.matches(".*[?#%&!@$^*()/]+."))
        {
            Toast.makeText(context, "Цех содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!departments.contains(department))
        {
            Toast.makeText(context, "Такого цеха не существует", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean IsValidLogin(String login) throws SQLException {
        if(login.isEmpty())
        {
            Toast.makeText(context, "Введите логин", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(login.matches(".*[?#%&!$^*()/]+."))
        {
            Toast.makeText(context, "Цех содержит недопустимые символы", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(!user_data.IsValidateLogin(login))
        {
            Toast.makeText(context, "Логин уже занят", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    public boolean IsValidPassword(String password)
    {
        if(password.isEmpty())
        {
            Toast.makeText(context, "Введите пароль", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(password.length() > user_data.MAX_LOGIN_LENGTH)
        {
            Toast.makeText(context, String.format(Locale.US, "Логин не должен превышать %d символов", user_data.MAX_LOGIN_LENGTH), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private boolean IsFindFilter(Employer employer, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return employer.getName().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getSurname().toLowerCase().contains(lowerCaseSearchString) ||
                employer.getJobTitle().toLowerCase().contains(lowerCaseSearchString) ||
                String.valueOf(employer.getAccountID()).equals(search_string);
    }
}
