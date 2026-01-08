package com.example.myoffice.ui.services.services_pages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.Login;
import com.example.myoffice.databinding.ServiceMessagesBinding;
import com.example.myoffice.databinding.ServiceStatementsBinding;
import com.example.myoffice.ui.adapters.MessageAdapter;
import com.example.myoffice.ui.adapters.PersonalStatementAdapter;
import com.example.myoffice.ui.adapters.StatementAdapter;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.classes.MessageClass;
import com.example.myoffice.ui.services.classes.ReportGenerator;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.fragments.ServiceMain;
import com.example.myoffice.user_data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceMessages
{
    static ResultSet result_query;
    public Context context;
    private RecyclerView recycler_messages;
    private MessageAdapter messageAdapter;
    private List<MessageClass> messages;

    public ServiceMessages(Context context)
    {
        this.context = context;
    }


    public boolean LoadMessages(ServiceMessagesBinding binding_messages) throws SQLException
    {
        if (DataBase.connection != null)
        {
            String query;
            Statement statement;

            query = String.format(Locale.US, "SELECT t1.id, t1.accountid, t1.type, t1.title, t1.message, t1.send_accountid, t1.send_date, t1.is_read, t2.surname, t2.name " +
                    "FROM messages AS t1 LEFT JOIN accounts AS t2 ON t1.send_accountid = t2.id WHERE t1.accountid = %d ORDER BY t1.`is_read` ASC", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

            statement = DataBase.connection.createStatement();
            result_query = statement.executeQuery(query);

            recycler_messages = binding_messages.recyclerMessages;
            messages = new ArrayList<>();

            int messageid, accountid, type, send_accountid, is_read;
            String surname = "", name = "", title = "", message = "";
            Date send_date;

            while (result_query.next())
            {
                messageid = result_query.getInt("id");
                accountid = result_query.getInt("accountid");
                type = result_query.getInt("type");
                send_accountid = result_query.getInt("send_accountid");
                is_read = result_query.getInt("is_read");
                title = result_query.getString("title");
                message = result_query.getString("message");
                surname = result_query.getString("surname");
                name = result_query.getString("name");
                send_date = result_query.getDate("send_date");

                messages.add(new MessageClass(messageid, type, accountid, send_accountid, is_read, title, message, surname + ' ' + name, send_date));
            }

            // Пример адаптера для RecyclerView
            messageAdapter = new MessageAdapter(messages, false, messageclass -> {
                ServiceMain.ShowServiceMessageInfo(messageclass.getMessageID(), messageclass.isRead(), messageclass.getTitle(), messageclass.getMessage(), messageclass.getSendFullName(), messageclass.getSendDate());
            });
            recycler_messages.setLayoutManager(new LinearLayoutManager(context));
            recycler_messages.setAdapter(messageAdapter);

            Button send_new_message = binding_messages.sendNewNotification;

            if(!user_data.IsAllowRankGiveCodes((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK)))
                send_new_message.setVisibility(View.INVISIBLE);

            send_new_message.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Login.playClickSound(context, "click_tap");

                    ServiceMain.ShowServiceMessageInput();
                }
            });
            return true;
        }
        return false;
    }
}
