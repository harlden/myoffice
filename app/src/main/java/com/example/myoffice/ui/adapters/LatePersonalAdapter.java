package com.example.myoffice.ui.adapters;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.LateClass;

import java.util.List;

public class LatePersonalAdapter extends RecyclerView.Adapter<LatePersonalAdapter.ViewHolder>
{

    private final List<LateClass> lateClasses;

    public LatePersonalAdapter(List<LateClass> lateClasses) {
        this.lateClasses = lateClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_personal_late, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LateClass lateClass = lateClasses.get(position);
        holder.latePersonalFio.setText("Опоздание №" + lateClass.getLateID());
        holder.latePersonalArrivalTime.setText("Будете " + lateClass.getDateLate().toString() + " в " + lateClass.getArrivalTime());
        holder.latePersonalDate.setText("Комментарий: " + lateClass.getComment());
    }

    @Override
    public int getItemCount() {
        return lateClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView latePersonalFio;
        TextView latePersonalArrivalTime;
        TextView latePersonalDate;
        LinearLayout containerStatement; // Добавьте это

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            latePersonalFio = itemView.findViewById(R.id.latePersonalFio);
            latePersonalArrivalTime = itemView.findViewById(R.id.latePersonalArrivalTime);
            latePersonalDate = itemView.findViewById(R.id.latePersonalDate);
            containerStatement = itemView.findViewById(R.id.ContainerStatement); // Инициализация
        }
    }

    public void updateList(List<LateClass> newList) {
        lateClasses.clear();
        lateClasses.addAll(newList);
        notifyDataSetChanged();
    }
}
