package com.example.myoffice.ui.services.services_pages;

import android.content.Context;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.Login;
import com.example.myoffice.databinding.ServiceCodesBinding;
import com.example.myoffice.ui.adapters.KeyAdapter;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ServiceCodes implements KeyAdapter.OnKeyRemoveListener
{
    public static List<Integer> keys;
    public static KeyAdapter keyAdapter;
    static ResultSet result_query;
    public EditText add_code_field;
    public Button add_code_button;
    public RecyclerView list_codes;
    public Context context;

    public ServiceCodes(Context context)
    {
        this.context = context;
    }

    public void onKeyRemove(int key) throws SQLException
    {
        int position = keys.indexOf(key);

        if (position >= 0 && DeleteCode(key))
        {
            keys.remove(position);
            keyAdapter.notifyItemRemoved(position);

            Toast.makeText(context, String.format("Вы удалили ключ доступа `%d`", key), Toast.LENGTH_SHORT).show();
        }
    }

    public boolean LoadCodes(ServiceCodesBinding binding_codes) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = "SELECT * FROM `codes`";

            Statement statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            add_code_field = binding_codes.editTextKey;
            add_code_button = binding_codes.buttonAdd;
            list_codes = binding_codes.recyclerView;

            keys = new ArrayList<>();

            while (result_query.next())
            {
                keys.add(result_query.getInt("code"));
            }

            keyAdapter = new KeyAdapter(keys, this);

            list_codes.setLayoutManager(new LinearLayoutManager(context));
            list_codes.setAdapter(keyAdapter);

            add_code_button.setOnClickListener(v ->
            {
                Login.playClickSound(context, "click_tap");
                String input_code = add_code_field.getText().toString();

                if (!input_code.isEmpty() && input_code.length() <= 4)
                {
                    int new_code = Integer.parseInt(input_code);

                    if(!keys.contains(new_code))
                    {
                        try
                        {
                            if (AddCodes(new_code))
                            {
                                keys.add(new_code);
                                keyAdapter.notifyItemInserted(keys.size() - 1);

                                add_code_field.setText(""); // Очистка поля ввода
                                Toast.makeText(context, "Вы успешно добавили ключ доступа", Toast.LENGTH_SHORT).show();
                            }
                            else Toast.makeText(context, "Ключ доступа не получилось добавить", Toast.LENGTH_SHORT).show();
                        } catch (SQLException e) {
                            throw new RuntimeException(e);
                        }
                    }
                    else Toast.makeText(context, "Ключ доступа уже добавлен", Toast.LENGTH_SHORT).show();
                }
                else Toast.makeText(context, "Введите ключ доступа до 4х символов", Toast.LENGTH_SHORT).show();
            });
            return true;
        }
        return false;
    }

    public boolean AddCodes(int code) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US,"INSERT INTO `codes` (`code`) VALUES ('%d')", code);

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

    public boolean DeleteCode(int code) throws SQLException
    {
        if(DataBase.connection != null)
        {
            String query = String.format(Locale.US,"DELETE FROM `codes` WHERE `code` = '%d'", code);

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
}
