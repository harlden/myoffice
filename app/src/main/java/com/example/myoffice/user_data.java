package com.example.myoffice;

import android.annotation.SuppressLint;
import android.util.Log;

import com.example.myoffice.ui.services.classes.Employer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import com.google.firebase.messaging.FirebaseMessaging;

public class user_data {
    public static final int MAX_SURNAME_LENGTH = 16;
    public static final int MAX_NAME_LENGTH = 16;
    public static final int MAX_LOGIN_LENGTH = 16;
    public static final int MAX_PASSWORD_LENGTH = 16;

    public enum E_USER_DATA
    {
        DATA_ID,
        DATA_LOGIN,
        DATA_PASSWORD,
        DATA_NAME,
        DATA_SURNAME,
        DATA_JOB_RANK,
        DATA_DATE_INVITE,
        DATA_ORDER_INVITE,
        DATA_TOKEN_NOTIFICATION,
        DATA_SCHEDULE_WORK_DAY_FIRST,
        DATA_SCHEDULE_WORK_DAY_SECOND,
        //
        DATA_COUNT
    };

    private static final int MAX_USER_DATA = E_USER_DATA.values().length;
    public static ResultSet result_query;
    public static String[] job_name = new String[] {"Рабочий", "Инженер", "Экономист", "Сотрудник отдела кадров", "Начальник цеха", "Директор завода"};

    public static Object[] user_data = new Object[MAX_USER_DATA];

    public static Object GetUserData(E_USER_DATA index_data)
    {
        return user_data[index_data.ordinal()];
    }

    public static void SetUserData(E_USER_DATA index_data, Object value)
    {
        user_data[index_data.ordinal()] = value;
    }

    public static String GetJobTitle(int job_rank)
    {
        return job_name[job_rank];
    }

    public static int GetJobRank(String job_title)
    {
        for(int i = 0; i < job_name.length; i++)
        {
            if(Objects.equals(job_name[i], job_title))
                return i;
        }
        return 0;
    }

    public static boolean IsExistsJobRank(String job_title)
    {
        for(int i = 0; i < job_name.length; i++)
        {
            if(Objects.equals(job_name[i], job_title))
                return true;
        }
        return false;
    }

    public static boolean IsAllowRankGiveCodes(int job_rank)
    {
        if(job_name[job_rank] == "Сотрудник отдела кадров")
            return true;

        return false;
    }

    public static boolean LoadUserData(String login, String password) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format("SELECT * FROM `accounts` WHERE `login` = '%s' AND `password` = '%s'", login, password);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            if (result_query.next()) {
                do {
                    user_data[E_USER_DATA.DATA_ID.ordinal()] = result_query.getObject("id");
                    user_data[E_USER_DATA.DATA_LOGIN.ordinal()] = result_query.getObject("login");
                    user_data[E_USER_DATA.DATA_PASSWORD.ordinal()] = result_query.getObject("password");
                    user_data[E_USER_DATA.DATA_NAME.ordinal()] = result_query.getObject("name");
                    user_data[E_USER_DATA.DATA_SURNAME.ordinal()] = result_query.getObject("surname");
                    user_data[E_USER_DATA.DATA_JOB_RANK.ordinal()] = result_query.getObject("job_rank");
                    user_data[E_USER_DATA.DATA_DATE_INVITE.ordinal()] = result_query.getObject("date_invite");
                    user_data[E_USER_DATA.DATA_ORDER_INVITE.ordinal()] = result_query.getObject("order_invite");
                    user_data[E_USER_DATA.DATA_TOKEN_NOTIFICATION.ordinal()] = result_query.getObject("token_notification");
                    user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_FIRST.ordinal()] = result_query.getObject("schedule_work_day_first");
                    user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_SECOND.ordinal()] = result_query.getObject("schedule_work_day_second");

                } while (result_query.next());
                return true;
            }
        }
        return false;
    }

    public static boolean AddUserData(String login, String password, int code, String token_notification) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format("INSERT INTO `accounts` (`login`, `password`, `token_notification`) VALUES ('%s', '%s', '%s')", login, password, token_notification);

            Statement statement = DataBase.connection.createStatement();
            int rowsAffected = statement.executeUpdate(query);

            if (rowsAffected > 0)
            {
                result_query = statement.executeQuery("SELECT LAST_INSERT_ID()");
                if (result_query.next())
                {
                    do {
                        user_data[E_USER_DATA.DATA_ID.ordinal()] = result_query.getObject(1);
                        user_data[E_USER_DATA.DATA_LOGIN.ordinal()] = login;
                        user_data[E_USER_DATA.DATA_PASSWORD.ordinal()] = password;
                        user_data[E_USER_DATA.DATA_NAME.ordinal()] = "None";
                        user_data[E_USER_DATA.DATA_SURNAME.ordinal()] = "None";
                        user_data[E_USER_DATA.DATA_JOB_RANK.ordinal()] = 0;
                        user_data[E_USER_DATA.DATA_DATE_INVITE.ordinal()] = "1970-01-01";
                        user_data[E_USER_DATA.DATA_ORDER_INVITE.ordinal()] = '0';
                        user_data[E_USER_DATA.DATA_TOKEN_NOTIFICATION.ordinal()] = token_notification;
                        user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_FIRST.ordinal()] = 5;
                        user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_SECOND.ordinal()] = 2;
                    } while (result_query.next());

                    statement.executeQuery(String.format(Locale.US, "DELETE FROM `codes` WHERE `code` = '%d'", code));
                    return true;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    public static void SaveUserData(E_USER_DATA index_data, Object value) throws SQLException
    {
        user_data[index_data.ordinal()] = value;

        @SuppressLint("DefaultLocale") String query = String.format(
                "UPDATE `accounts` SET " +
                        "`login` = '%s'," +
                        "`password` = '%s'," +
                        "`name` = '%s'," +
                        "`surname` = '%s'," +
                        "`job_rank` = '%d', " +
                        "`date_invite` = '%s', " +
                        "`order_invite` = '%s', " +
                        "`token_notification` = '%s', " +
                        "`schedule_work_day_first` = '%d', " +
                        "`schedule_work_day_second` = '%d' " +
                        "WHERE `id` = '%d'",
                String.valueOf(user_data[E_USER_DATA.DATA_LOGIN.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_PASSWORD.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_NAME.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_SURNAME.ordinal()]),
                ((Integer) user_data[E_USER_DATA.DATA_JOB_RANK.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_DATE_INVITE.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_ORDER_INVITE.ordinal()]),
                String.valueOf(user_data[E_USER_DATA.DATA_TOKEN_NOTIFICATION.ordinal()]),
                ((Integer) user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_FIRST.ordinal()]),
                ((Integer) user_data[E_USER_DATA.DATA_SCHEDULE_WORK_DAY_SECOND.ordinal()]),
                ((Integer) user_data[E_USER_DATA.DATA_ID.ordinal()]));

        Statement statement = DataBase.connection.createStatement();
        statement.executeQuery(query);
    }

    public static boolean IsValidateCode(int code) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "SELECT * FROM `codes` WHERE `code` = '%d' LIMIT 1", code);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            return result_query.next();
        }
        return false;
    }

    public static boolean IsValidateLogin(String login) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US, "SELECT * FROM `accounts` WHERE `login` = '%s' LIMIT 1", login);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            return !result_query.next();
        }
        return false;
    }

    public static int GetAccountIDOfFullName(String full_name) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String[] parts = full_name.split(" ");
            String query = String.format(Locale.US, "SELECT * FROM `accounts` WHERE `name` = '%s' AND `surname` = '%s' LIMIT 1",
                    parts[1], parts[0]);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            while(result_query.next())
            {
                return result_query.getInt("id");
            }
        }
        return -1;
    }

    public static String GetFullNameOfAccountID(int accountid) throws SQLException
    {
        String full_name = "";
        if (DataBase.connection != null) {
            String query = String.format(Locale.US, "SELECT * FROM `accounts` WHERE `id` = '%d' LIMIT 1", accountid);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            while (result_query.next()) {
                // Правильное заполнение full_name с использованием +=
                full_name += result_query.getString("surname") + " " + result_query.getString("name");
            }
        }
        return full_name;
    }

    public static String GetTokenOfAccountID(int accountid) throws SQLException
    {
        String token_notification = "";
        if (DataBase.connection != null) {
            String query = String.format(Locale.US, "SELECT `token_notification` FROM `accounts` WHERE `id` = '%d' LIMIT 1", accountid);

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            while (result_query.next()) {
                // Правильное заполнение full_name с использованием +=
                token_notification += result_query.getString("token_notification");
            }
        }
        return token_notification;
    }

    public static void LoadAllEmployers(List<String> employers_name, boolean is_check_token) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = "";

            if(!is_check_token)
                query = "SELECT * FROM `accounts`";
            else
                query = "SELECT * FROM `accounts` WHERE `token_notification` != 'null'";

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            while (result_query.next())
            {
                String name = result_query.getString("name");
                String surname = result_query.getString("surname");

                employers_name.add(surname + " " + name);
            }
        }
    }
}
