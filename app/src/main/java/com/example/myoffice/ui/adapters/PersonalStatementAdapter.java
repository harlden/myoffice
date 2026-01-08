package com.example.myoffice.ui.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.StatementClass;

import java.util.List;

public class PersonalStatementAdapter extends RecyclerView.Adapter<PersonalStatementAdapter.ViewHolder> {

    private List<StatementClass> personalStatementClasses;
    private OnPersonalStatementClickListener listener;
    private Context context;
    private boolean is_hide_button;

    public PersonalStatementAdapter(List<StatementClass> personalStatementClasses, Context context, boolean is_hide_button, OnPersonalStatementClickListener listener) {
        this.personalStatementClasses = personalStatementClasses;
        this.listener = listener;
        this.context = context;
        this.is_hide_button = is_hide_button;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_statement, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StatementClass statementClass = personalStatementClasses.get(position);
        holder.statementType.setText("№" + statementClass.getStatementID() + " " + statementClass.getStatementName());
        holder.statementDates.setText("с " + statementClass.getStartDate() + " по " + statementClass.getEndDate());

        if(statementClass.isAccept()) {
            holder.statementStatus.setText("Рассмотрено");
            holder.statementStatus.setTextColor(ContextCompat.getColor(context, R.color.main_green));
        }

        holder.viewButton.setOnClickListener(v -> listener.onViewClick(statementClass));

        if(is_hide_button)
        {
            holder.viewButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return personalStatementClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView statementType;
        TextView statementDates;
        TextView statementStatus;
        ImageView viewButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            statementType = itemView.findViewById(R.id.statement_type);
            statementDates = itemView.findViewById(R.id.statement_dates);
            statementStatus = itemView.findViewById(R.id.statement_status);
            viewButton = itemView.findViewById(R.id.statement_button_view);
        }
    }

    public interface OnPersonalStatementClickListener {
        void onViewClick(StatementClass statementClass);
    }

    public void updateList(List<StatementClass> newList) {
        personalStatementClasses.clear();
        personalStatementClasses.addAll(newList);
        notifyDataSetChanged();
    }
}
