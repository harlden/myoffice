package com.example.myoffice;

import android.os.StrictMode;
import android.util.Log;
import java.sql.*;
import java.util.Objects;

public class DataBase {
    protected static String database_user = "denis";
    protected static String database_password = "fYGR138XtFdY";
    public static Connection connection = null;

    public Connection connectSQL()
    {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        Connection result_connection = null;
        try
        {
            Class.forName("org.mariadb.jdbc.Driver");
            String connection_url = "jdbc:mysql://185.139.70.104:3306/office_employee_zlin?characterEncoding=utf8";
            result_connection = DriverManager.getConnection(connection_url, database_user, database_password);
        }
        catch (Exception e)
        {
            Log.e("ERROR", Objects.requireNonNull(e.getMessage()));
        }

        return result_connection;
    }
}
