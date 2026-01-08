package com.example.myoffice.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.StatementClass;

import java.util.List;

public class StatementAdapter extends RecyclerView.Adapter<StatementAdapter.ViewHolder>
{

    private List<StatementClass> statementClasses;
    private OnStatementClickListener listener;
    private static boolean is_hide_button;

    public StatementAdapter(List<StatementClass> statementClasses, boolean is_hide_button, OnStatementClickListener listener) {
        this.statementClasses = statementClasses;
        this.listener = listener;
        this.is_hide_button = is_hide_button;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_statement, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatementClass statementClass = statementClasses.get(position);
        holder.statementType.setText("№" + statementClass.getStatementID() + " " + statementClass.getStatementName());
        holder.statementFio.setText(statementClass.getFullName());
        holder.statementDates.setText("с " + statementClass.getStartDate() + " по " + statementClass.getEndDate());

        holder.viewButton.setOnClickListener(v -> listener.onViewClick(statementClass));
        holder.printButton.setOnClickListener(v -> listener.onPrintClick(statementClass));

        if(is_hide_button)
        {
            holder.viewButton.setVisibility(View.INVISIBLE);
            holder.printButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return statementClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView statementType;
        TextView statementFio;
        TextView statementDates;
        ImageView viewButton, printButton;
        LinearLayout containerStatement; // Добавьте это

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statementType = itemView.findViewById(R.id.statement_type);
            statementFio = itemView.findViewById(R.id.statement_fio);
            statementDates = itemView.findViewById(R.id.statement_dates);
            viewButton = itemView.findViewById(R.id.statement_button_view);
            printButton = itemView.findViewById(R.id.statement_button_print);
            containerStatement = itemView.findViewById(R.id.ContainerStatement); // Инициализация
        }
    }

    public interface OnStatementClickListener {
        void onViewClick(StatementClass statement);
        void onPrintClick(StatementClass statement); // Убедитесь, что этот метод тоже присутствует
    }

    public void updateList(List<StatementClass> newList) {
        statementClasses.clear();
        statementClasses.addAll(newList);
        notifyDataSetChanged();
    }
}
