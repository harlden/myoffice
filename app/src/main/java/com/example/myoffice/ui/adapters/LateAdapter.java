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
import com.example.myoffice.ui.services.classes.LateClass;
import com.example.myoffice.ui.services.classes.StatementClass;

import java.util.List;

public class LateAdapter extends RecyclerView.Adapter<LateAdapter.ViewHolder>
{

    private List<LateClass> lateClasses;

    public LateAdapter(List<LateClass> lateClasses) {
        this.lateClasses = lateClasses;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_late, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LateClass lateClass = lateClasses.get(position);
        holder.lateFio.setText("№" + lateClass.getLateID() + " " + lateClass.getFullName());
        holder.lateArrivalTime.setText("Будет " + lateClass.getDateLate().toString() + " в " + lateClass.getArrivalTime());
        holder.lateDate.setText("Комментарий: " + lateClass.getComment());
    }

    @Override
    public int getItemCount() {
        return lateClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView lateFio;
        TextView lateArrivalTime;
        TextView lateDate;
        LinearLayout containerStatement; // Добавьте это

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            lateFio = itemView.findViewById(R.id.lateFio);
            lateArrivalTime = itemView.findViewById(R.id.lateArrivalTime);
            lateDate = itemView.findViewById(R.id.lateDate);
            containerStatement = itemView.findViewById(R.id.ContainerStatement); // Инициализация
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateList(List<LateClass> newList) {
        lateClasses.clear();
        lateClasses.addAll(newList);
        notifyDataSetChanged();
    }
}
