package com.example.myoffice.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.Employer;

import java.util.List;
import java.util.Locale;

public class EmployerAdapter extends RecyclerView.Adapter<EmployerAdapter.ViewHolder> {

    private static List<Employer> employers;
    private static boolean is_hide_button;
    private OnEmployerClickListener listener;

    public interface OnEmployerClickListener {
        void onEditClick(Employer employer);
        void onDeleteClick(Employer employer);
    }

    public EmployerAdapter(List<Employer> employers, boolean is_hide_button, OnEmployerClickListener listener) {
        this.employers = employers;
        this.listener = listener;
        this.is_hide_button = is_hide_button;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_employer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Employer employer = employers.get(position);
        holder.fullName.setText(String.format(Locale.US, "%s (UID: %d)", employer.getFullName(), employer.getAccountID()));
        holder.job_title.setText(employer.getJobTitle());
        holder.date_invite.setText(String.format(Locale.US, "Дата принятия: %s (№%s)", employer.getDateInvite(), employer.getOrderInvite()));

        holder.editButton.setOnClickListener(v -> listener.onEditClick(employer));
        holder.deleteButton.setOnClickListener(v -> listener.onDeleteClick(employer));

        if(is_hide_button) {
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
        TextView job_title;
        TextView date_invite;
        ImageView editButton;
        ImageView deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fullName = itemView.findViewById(R.id.employer_fio);
            job_title = itemView.findViewById(R.id.employer_job_title);
            date_invite = itemView.findViewById(R.id.employer_date_invite);
            editButton = itemView.findViewById(R.id.employer_button_edit);
            deleteButton = itemView.findViewById(R.id.employer_button_delete);
        }
    }

    public void updateList(List<Employer> newList) {
        employers.clear(); // Очистка текущего списка
        employers.addAll(newList); // Добавление новых данных
        notifyDataSetChanged(); // Уведомление адаптера об изменении данных
    }
}
