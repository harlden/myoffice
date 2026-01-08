package com.example.myoffice.ui.services.services_pages;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.DataBase;
import com.example.myoffice.EditTextDialog;
import com.example.myoffice.EditTextItem;
import com.example.myoffice.Login;
import com.example.myoffice.R;
import com.example.myoffice.databinding.ServiceStatementsBinding;
import com.example.myoffice.ui.adapters.EmployerAdapter;
import com.example.myoffice.ui.adapters.PersonalStatementAdapter;
import com.example.myoffice.ui.adapters.StatementAdapter;
import com.example.myoffice.ui.services.classes.Employer;
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

public class ServiceStatements
{
    static ResultSet result_query;
    public Context context;
    private RecyclerView statementAllRecyclerView, statementMyRecyclerView;
    private StatementAdapter statementAllAdapter;
    private PersonalStatementAdapter statementMyAdapter;
    private List<StatementClass> statementsAll, statementsMy;

    public ServiceStatements(Context context)
    {
        this.context = context;
    }


    public boolean LoadStatements(ServiceStatementsBinding binding_statements) throws SQLException {
        if (DataBase.connection != null)
        {
            String query;
            Statement statementClass;

            if(user_data.IsAllowRankGiveCodes((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK)))
            {
                query = "SELECT t2.id, t1.surname, t1.name, t2.statement_type, t2.date_start, t2.date_end, t2.comment, t2.date_create, t2.is_accept, t2.accountid, t1.token_notification FROM " +
                        "accounts AS t1 LEFT JOIN `statements` AS t2 ON t2.accountid = t1.id WHERE t2.accountid = t1.id AND t2.is_accept = 0";

                statementClass = DataBase.connection.createStatement();
                result_query = statementClass.executeQuery(query);

                statementAllRecyclerView = binding_statements.recyclerStatements;
                statementsAll = new ArrayList<>();

                String surname = "", name = "", comment = "", token_notification = "";
                int statementType = 0, statementID = 0, is_accept = 0, accountid = 0;
                Date dateStart, dateEnd, dateCreate;

                while (result_query.next())
                {
                    surname = result_query.getString("surname");
                    name = result_query.getString("name");
                    comment = result_query.getString("comment");
                    dateStart = result_query.getDate("date_start");
                    token_notification = result_query.getString("token_notification");
                    dateEnd = result_query.getDate("date_end");
                    dateCreate = result_query.getDate("date_create");
                    statementID = result_query.getInt("id");
                    statementType = result_query.getInt("statement_type");
                    is_accept = result_query.getInt("is_accept");
                    accountid = result_query.getInt("accountid");

                    statementsAll.add(new StatementClass(statementID, statementType, (surname + " " + name),
                            dateStart, dateEnd, comment, dateCreate, is_accept, token_notification));
                }

                int finalAccountid = accountid;

                statementAllAdapter = new StatementAdapter(statementsAll, false, new StatementAdapter.OnStatementClickListener() {
                    @Override
                    public void onViewClick(StatementClass statement)
                    {
                        Login.playClickSound(context, "click_tap");
                        ServiceMain.ShowServiceStatementConfirm(
                                finalAccountid,
                                statement.getStatementID(),
                                statement.getStatementName(),
                                statement.getFullName(),
                                statement.getStartDate(),
                                statement.getEndDate(),
                                statement.getCreateDate(),
                                statement.getComment(),
                                statement.getTokenNotification()
                        );
                    }

                    @Override
                    public void onPrintClick(StatementClass statement) {
                        Login.playClickSound(context, "click_tap");
                        showConfirmPrint(statement, true);
                    }
                });

                statementAllRecyclerView.setLayoutManager(new LinearLayoutManager(context));
                statementAllRecyclerView.setAdapter(statementAllAdapter);
            }
            else
            {
                LinearLayout containerAllStatements = binding_statements.containerAllStatements;
                containerAllStatements.setVisibility(View.GONE);

                TextView labelMyStatements = binding_statements.labelMyStatements;

                ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) labelMyStatements.getLayoutParams();

                params.topMargin = 0; // Устанавливаем верхний отступ в 0
                labelMyStatements.setLayoutParams(params);
            }

            query = String.format(Locale.US, "SELECT t1.id, t1.statement_type, t1.date_start, t1.date_end, t1.comment, t1." +
                    "date_create, t1.is_accept, t2.surname, t2.name FROM statements AS t1 LEFT JOIN accounts AS t2 ON t1.accountid = t2.id WHERE accountid = '%d'", (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID));

            statementClass = DataBase.connection.createStatement();
            result_query = statementClass.executeQuery(query);

            statementMyRecyclerView = binding_statements.recyclerMyStatements;
            statementsMy = new ArrayList<>();

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

                statementsMy.add(new StatementClass(statementID, statementType, surname + " " + name, dateStart, dateEnd,
                        comment, dateCreate, is_accept, ""));
            }

            // Пример адаптера для RecyclerView
            statementMyAdapter = new PersonalStatementAdapter(statementsMy, context, false, statementClass1 -> {
                showConfirmPrint(statementClass1, false);
            });
            statementMyRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            statementMyRecyclerView.setAdapter(statementMyAdapter);

            Button add_statement = binding_statements.applyButton;
            add_statement.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Login.playClickSound(context, "click_tap");
                    int job_rank = (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_JOB_RANK);

                    Employer employer = new Employer((Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_ID),
                            (String) user_data.GetUserData(user_data.E_USER_DATA.DATA_NAME),
                            (String) user_data.GetUserData(user_data.E_USER_DATA.DATA_SURNAME),
                            user_data.GetJobTitle(job_rank),
                            user_data.GetUserData(user_data.E_USER_DATA.DATA_DATE_INVITE).toString(),
                            (String) user_data.GetUserData(user_data.E_USER_DATA.DATA_ORDER_INVITE), 5, 2);

                    ServiceMain.ShowServiceStatementInput(employer);
                }
            });
            return true;
        }
        return false;
    }

    private void showConfirmPrint(StatementClass statement, boolean is_accept_statements)
    {
        if(statement.isAccept() || is_accept_statements)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Подтверждение печати")
                    .setMessage(String.format(Locale.US, "Вы уверены, что хотите распечатать заявление №%d?", statement.getStatementID()))
                    .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ReportGenerator.GeneratePersonalStatement(context, statement);
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
        else Toast.makeText(context, "Заявление должно быть рассмотрено", Toast.LENGTH_SHORT).show();
    }
}
