package com.example.myoffice.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.ActionEmployer;
import com.example.myoffice.ui.services.classes.Employer;

import java.util.List;
import java.util.Locale;

public class EmployerActionsAdapter extends RecyclerView.Adapter<EmployerActionsAdapter.ViewHolder> {

    private static List<ActionEmployer> employers;
    private OnEmployerActionClickListener listener;
    private static boolean is_hide_buttons;

    public interface OnEmployerActionClickListener {
        void onEditClick(ActionEmployer employer);
        void onDeleteClick(ActionEmployer employer);
    }

    public EmployerActionsAdapter(List<ActionEmployer> employers, boolean is_hide_buttons, OnEmployerActionClickListener listener) {
        this.employers = employers;
        this.listener = listener;
        this.is_hide_buttons = is_hide_buttons;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ActionEmployer employer = employers.get(position);
        holder.fullName.setText(String.format(Locale.US, "%s (UID: %d)", employer.getFullName(), employer.getAccountID()));

        holder.action_name.setText(employer.getActionTypeName(employer.getActionType()));
        holder.date_action.setText(String.format(Locale.US, "Дата: %s (№%s)", employer.getDate(), employer.getOrder()));

        holder.editButton.setOnClickListener(v -> listener.onEditClick(employer));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(employer));

        if(is_hide_buttons)
        {
            holder.editButton.setVisibility(View.INVISIBLE);
            holder.deleteButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return employers.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView avatar;
        TextView fullName;
        TextView action_name;
        TextView date_action;
        ImageView editButton;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.employer_action_fio);
            action_name = itemView.findViewById(R.id.employer_action_name);
            date_action = itemView.findViewById(R.id.employer_date_action);
            editButton = itemView.findViewById(R.id.employer_button_edit);
            deleteButton = itemView.findViewById(R.id.employer_button_delete);
        }
    }

    public void updateList(List<ActionEmployer> newList) {
        employers.clear(); // Очистка текущего списка
        employers.addAll(newList); // Добавление новых данных
        notifyDataSetChanged(); // Уведомление адаптера об изменении данных
    }
}
