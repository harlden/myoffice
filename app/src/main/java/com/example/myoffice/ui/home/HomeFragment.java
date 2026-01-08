package com.example.myoffice.ui.home;

import static com.example.myoffice.ui.services.fragments.ServiceMain.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.R;
import com.example.myoffice.databinding.FragmentHomeBinding;
import com.example.myoffice.ui.adapters.MessageAdapter;
import com.example.myoffice.ui.adapters.PersonalStatementAdapter;
import com.example.myoffice.ui.services.classes.MessageClass;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;
import com.example.myoffice.ui.services.fragments.ServiceMain;
import com.example.myoffice.user_data;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment {
    private boolean isMessageExpanded = false;
    private boolean isStatementExpanded = false;
    private boolean isLatesExpanded = false;
    private FragmentHomeBinding binding;
    static ResultSet result_query;
    public Context context;
    private RecyclerView recycler_message, recycler_statement;
    private MessageAdapter messageAdapter;
    private PersonalStatementAdapter statementAdapter;
    private List<MessageClass> messages;
    private List<StatementClass> statements;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView helloTitle = binding.helloTitle;
        TextView currentDate = binding.currentDay;

        context = requireContext();

        helloTitle.setText(String.format(Locale.US, "Здравствуйте, %s", user_data.GetUserData(user_data.E_USER_DATA.DATA_NAME)));

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, d MMMM yyyy", new Locale("ru"));
        currentDate.setText(dateFormat.format(new Date()));

        binding.messageContent.setVisibility(View.VISIBLE);
        binding.ivMessageArrow.setImageResource(R.drawable.ic_up_arrow_foreground);

        if (DataBase.connection != null) {
            String query;
            Statement statement;

            query = String.format(Locale.US, "SELECT t1.id, t1.accountid, t1.type, t1.title, t1.message, t1.send_accountid, t1.send_date, t1.is_read, t2.surname, t2.name " +
                    "FROM messages AS t1 LEFT JOIN accounts AS t2 ON t1.send_accountid = t2.id WHERE t1.accountid = %d ORDER BY t1.`id` DESC LIMIT 1", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

            try {
                statement = DataBase.connection.createStatement();
                result_query = statement.executeQuery(query);

                recycler_message = binding.recyclerMessage;
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
                messageAdapter = new MessageAdapter(messages, true, messageclass -> {

                });
                recycler_message.setLayoutManager(new LinearLayoutManager(context));
                recycler_message.setAdapter(messageAdapter);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            query = String.format(Locale.US, "SELECT t1.id, t1.statement_type, t1.date_start, t1.date_end, t1.comment, t1." +
                    "date_create, t1.is_accept, t2.surname, t2.name FROM statements AS t1 LEFT JOIN accounts AS t2 ON t1.accountid = t2.id WHERE accountid = '%d' ORDER BY t1.id DESC LIMIT 1", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

            try {
                statement = DataBase.connection.createStatement();
                result_query = statement.executeQuery(query);

                recycler_statement = binding.recyclerStatement;
                statements = new ArrayList<>();

                int statementType = 0, statementID = 0, is_accept = 0;
                Date dateStart, dateEnd, dateCreate;
                String comment, surname, name;

                while (result_query.next())
                {
                    statementID = result_query.getInt("id");
                    statementType = result_query.getInt("statement_type");
                    dateStart = result_query.getDate("date_start");
                    dateEnd = result_query.getDate("date_end");
                    dateCreate = result_query.getDate("date_create");
                    comment = result_query.getString("comment");
                    is_accept = result_query.getInt("is_accept");
                    surname = result_query.getString("surname");
                    name = result_query.getString("name");

                    statements.add(new StatementClass(statementID, statementType, surname + " " + name, dateStart, dateEnd,
                            comment, dateCreate, is_accept, ""));
                }

                // Пример адаптера для RecyclerView
                statementAdapter = new PersonalStatementAdapter(statements, context, true, statementClass1 -> {

                });
                recycler_statement.setLayoutManager(new LinearLayoutManager(context));
                recycler_statement.setAdapter(statementAdapter);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        // Обработка кликов по разделам
        setupExpandableSection(root, binding.ivMessageArrow, binding.messageContent, isMessageExpanded);
        setupExpandableSection(root, binding.ivStatementArrow, binding.statementContent, isStatementExpanded);
        setupExpandableSection(root, binding.ivLatesArrow, binding.latesContent, isLatesExpanded);

        TextView link_to_messages = binding.linkToMessages;
        TextView link_to_statements = binding.linkToStatements;
        TextView link_to_lates = binding.linkToLates;

        link_to_messages.setOnClickListener(v -> ShowServiceMessages());
        link_to_statements.setOnClickListener(v -> ShowServiceMessages());
        link_to_lates.setOnClickListener(v -> ShowServiceMessages());
        return root;
    }

    private void ShowServiceMessages()
    {
        assert getParentFragment() != null;
        NavController navController = NavHostFragment.findNavController(getParentFragment());
        Bundle bundle = new Bundle();

        NavOptions navOptions = new NavOptions.Builder()
                .setEnterAnim(R.anim.fade_in)
                .setExitAnim(R.anim.slide_out_left)
                .setPopEnterAnim(R.anim.fade_in)
                .setPopExitAnim(R.anim.fade_out)
                .build();

        navController.navigate(R.id.action_navigation_home_to_navigation_services, bundle, navOptions);
    }

    private void setupExpandableSection(View root, ImageView ivMessageArrow, LinearLayout messageContent, boolean isMessageExpanded) {
        ivMessageArrow.setOnClickListener(v -> {
            if (messageContent.getVisibility() == View.VISIBLE) {
                messageContent.setVisibility(View.GONE);
                ivMessageArrow.setImageResource(R.drawable.ic_down_arrow_foreground);
                animateRotation(ivMessageArrow, 0f);
            } else {
                messageContent.setVisibility(View.VISIBLE);
                ivMessageArrow.setImageResource(R.drawable.ic_up_arrow_foreground);
                animateRotation(ivMessageArrow, 180f);
            }
        });
    }

    private void animateRotation(ImageView imageView, float rotation) {
        imageView.animate()
                .rotation(rotation)
                .setDuration(200)
                .start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}