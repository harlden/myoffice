package com.example.myoffice.ui.services.services_pages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.Login;
import com.example.myoffice.databinding.ServiceLatesBinding;
import com.example.myoffice.databinding.ServiceStatementsBinding;
import com.example.myoffice.ui.FilterSort;
import com.example.myoffice.ui.adapters.LateAdapter;
import com.example.myoffice.ui.adapters.LatePersonalAdapter;
import com.example.myoffice.ui.adapters.PersonalStatementAdapter;
import com.example.myoffice.ui.adapters.StatementAdapter;
import com.example.myoffice.ui.services.classes.Employer;
import com.example.myoffice.ui.services.classes.LateClass;
import com.example.myoffice.ui.services.classes.ReportGenerator;
import com.example.myoffice.ui.services.classes.StatementClass;
import com.example.myoffice.ui.services.fragments.ServiceMain;
import com.example.myoffice.user_data;
import com.google.android.material.chip.ChipGroup;

import org.checkerframework.checker.units.qual.A;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ServiceLates
{
    static ResultSet result_query;
    public Context context;
    private RecyclerView latesAllRecyclerVeiw, latesMyRecyclerView;
    private static LateAdapter lateAllAdapter;
    private LatePersonalAdapter lateMyAdapter;
    private static List<LateClass> latesAll, latesMy;
    private List<LateClass> originalAllLates;

    public ServiceLates(Context context)
    {
        this.context = context;
    }


    public boolean LoadLates(ServiceLatesBinding binding_lates) throws SQLException
    {
        EditText search_field = binding_lates.searchAllLates;

        search_field.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Не используется
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Вызываем метод поиска при изменении текста
                OnFilterLates(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Не используется
            }
        });

        if (DataBase.connection != null)
        {
            String query;
            Statement statementClass;

            if(user_data.IsAllowRankGiveCodes((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK)))
            {
                query = String.format(Locale.US, "SELECT t2.id, t1.surname, t1.name, t2.date, t2.arrival_time, t2.comment, t2.accountid FROM " +
                        "accounts AS t1 LEFT JOIN `lates` AS t2 ON t2.accountid = t1.id WHERE t2.accountid = t1.id AND t2.accountid != %s ORDER BY `id` DESC", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

                statementClass = DataBase.connection.createStatement();
                result_query = statementClass.executeQuery(query);

                latesAllRecyclerVeiw = binding_lates.recyclerAllLates;
                latesAll = new ArrayList<>();

                String surname = "", name = "", comment = "";
                int lateID = 0, accountid = 0;
                Date date;
                Time arrival_time;

                while (result_query.next())
                {
                    surname = result_query.getString("surname");
                    name = result_query.getString("name");
                    comment = result_query.getString("comment");
                    date = result_query.getDate("date");
                    lateID = result_query.getInt("id");
                    accountid = result_query.getInt("accountid");
                    arrival_time = result_query.getTime("arrival_time");

                    latesAll.add(new LateClass(lateID, accountid, surname + ' ' + name, comment, date, arrival_time));
                }

                int finalAccountid = accountid;

                originalAllLates = new ArrayList<>(latesAll);
                lateAllAdapter = new LateAdapter(latesAll);

                latesAllRecyclerVeiw.setLayoutManager(new LinearLayoutManager(context));
                latesAllRecyclerVeiw.setAdapter(lateAllAdapter);

                FilterSort filterSort = new FilterSort(context, latesAllRecyclerVeiw, lateAllAdapter);
                filterSort.initialize(originalAllLates);

                // Настройка фильтров
                ChipGroup dateFilterGroup = binding_lates.dateFilterGroup;
                ChipGroup orderFilterGroup = binding_lates.orderFilterGroup;

                filterSort.setupDateFilters(dateFilterGroup);
                filterSort.setupOrderFilters(orderFilterGroup);
            }
            else
            {
                LinearLayout containerAllLates = binding_lates.containerAllLates;
                containerAllLates.setVisibility(View.GONE);

                TextView labelMyLates = binding_lates.labelMyLates;

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) labelMyLates.getLayoutParams();

                params.topMargin = 0; // Устанавливаем верхний отступ в 0
                labelMyLates.setLayoutParams(params);
            }

            query = String.format(Locale.US, "SELECT t2.id, t1.surname, t1.name, t2.date, t2.arrival_time, t2.comment, t2.accountid FROM " +
                    "accounts AS t1 LEFT JOIN `lates` AS t2 ON t2.accountid = t1.id WHERE t2.accountid = t1.id AND t2.accountid = '%d' ORDER BY `id` DESC", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

            statementClass = DataBase.connection.createStatement();
            result_query = statementClass.executeQuery(query);

            latesMyRecyclerView = binding_lates.recyclerMyLates;
            latesMy = new ArrayList<>();

            String surname = "", name = "", comment = "";
            int lateID = 0, accountid = 0;
            Date date;
            Time arrival_time;

            while (result_query.next())
            {
                surname = result_query.getString("surname");
                name = result_query.getString("name");
                comment = result_query.getString("comment");
                date = result_query.getDate("date");
                lateID = result_query.getInt("id");
                accountid = result_query.getInt("accountid");
                arrival_time = result_query.getTime("arrival_time");

                latesMy.add(new LateClass(lateID, accountid, surname + ' ' + name, comment, date, arrival_time));
            }

            // Пример адаптера для RecyclerView
            lateMyAdapter = new LatePersonalAdapter(latesMy);

            latesMyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            latesMyRecyclerView.setAdapter(lateMyAdapter);

            Button add_late = binding_lates.applyButton;
            add_late.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Login.playClickSound(context, "click_tap");
                    ServiceMain.ShowServiceLateInput();
                }
            });
            return true;
        }
        return false;
    }

    private void OnFilterLates(String search_string)
    {
        List<LateClass> filteredList = new ArrayList<>();

        if (!search_string.isEmpty()) {
            for (LateClass lateClass : originalAllLates)
            {
                if (IsFindLates(lateClass, search_string)) {
                    filteredList.add(lateClass);
                }
            }
        } else {
            filteredList.addAll(originalAllLates);
        }
        // Обновляем адаптер
        lateAllAdapter.updateList(filteredList);
    }

    private static boolean IsFindLates(LateClass lateClass, String search_string) {
        String lowerCaseSearchString = search_string.toLowerCase();

        // Проверяем, соответствует ли хотя бы одно из условий
        return lateClass.getFullName().toLowerCase().contains(lowerCaseSearchString) ||
                lateClass.getComment().toLowerCase().contains(lowerCaseSearchString);
    }
}
