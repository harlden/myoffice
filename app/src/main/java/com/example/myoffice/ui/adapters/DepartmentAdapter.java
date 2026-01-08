package com.example.myoffice.ui.adapters;

import android.content.Context;
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

public class DepartmentAdapter extends RecyclerView.Adapter<DepartmentAdapter.ViewHolder> {
    private List<String> departments;
    private OnDepartmentClickListener listener;
    public int selectedPosition = -1; // Позиция выбранного элемента

    public interface OnDepartmentClickListener {
        void onDepartmentClick(String department, int position) throws SQLException;
    }

    public DepartmentAdapter(List<String> departments, OnDepartmentClickListener listener, int selectedPosition) {
        this.departments = departments;
        this.listener = listener;
        this.selectedPosition = selectedPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_department, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String department = departments.get(position);
        holder.button.setText(department);

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
                listener.onDepartmentClick(department, currentPosition);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            notifyDataSetChanged(); // Уведомляем адаптер о необходимости обновления представления
        });
    }

    @Override
    public int getItemCount() {
        return departments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        Button button;

        ViewHolder(View itemView) {
            super(itemView);
            button = itemView.findViewById(R.id.buttonDepartment); // Измените на нужный ID
        }
    }
}
