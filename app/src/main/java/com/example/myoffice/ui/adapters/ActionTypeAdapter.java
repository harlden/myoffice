package com.example.myoffice.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;

import java.sql.SQLException;
import java.util.List;

public class ActionTypeAdapter extends RecyclerView.Adapter<ActionTypeAdapter.ViewHolder> {
    private List<String> action_name;
    private OnActionTypeClickListener listener;
    public int selectedPosition = -1; // Позиция выбранного элемента

    public interface OnActionTypeClickListener {
        void onActionTypeClick(String department, int position) throws SQLException;
    }

    public ActionTypeAdapter(List<String> action_name, OnActionTypeClickListener listener, int selectedPosition) {
        this.action_name = action_name;
        this.listener = listener;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_action_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String action_type = action_name.get(position);
        holder.button.setText(action_type);

        // Используйте getAdapterPosition(), чтобы получить текущую позицию
        int currentPosition = holder.getAdapterPosition();

        // Устанавливаем цвет фона в зависимости от выбранной позиции
        if (currentPosition == selectedPosition) {
            holder.button.setBackgroundColor(Color.RED); // Активная кнопка
        } else {
            holder.button.setBackgroundColor(Color.GRAY); // Неактивные кнопки
        }

        holder.button.setOnClickListener(v -> {
            selectedPosition = currentPosition; // Обновляем выбранную позицию
            try {
                listener.onActionTypeClick(action_type, currentPosition);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            notifyDataSetChanged(); // Уведомляем адаптер о необходимости обновления представления
        });
    }

    @Override
    public int getItemCount() {
        return action_name.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.buttonActionType); // Измените на нужный ID
        }
    }
}
