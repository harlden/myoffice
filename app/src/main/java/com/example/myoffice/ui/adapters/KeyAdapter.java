package com.example.myoffice.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;

import java.sql.SQLException;
import java.util.List;

public class KeyAdapter extends RecyclerView.Adapter<KeyAdapter.KeyViewHolder> {
    private final List<Integer> keys;
    private final OnKeyRemoveListener onKeyRemoveListener;

    public KeyAdapter(List<Integer> keys, OnKeyRemoveListener onKeyRemoveListener) {
        this.keys = keys;
        this.onKeyRemoveListener = onKeyRemoveListener;
    }

    @NonNull
    @Override
    public KeyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_code, parent, false);
        return new KeyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KeyViewHolder holder, int position) {
        int key = keys.get(position);
        holder.textViewKey.setText(String.valueOf(key));
        holder.buttonRemove.setOnClickListener(v ->
        {
            try
            {
                onKeyRemoveListener.onKeyRemove(key);
            } catch (SQLException e)
            {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    public static class KeyViewHolder extends RecyclerView.ViewHolder {
        TextView textViewKey;
        Button buttonRemove;

        public KeyViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewKey = itemView.findViewById(R.id.textViewKey);
            buttonRemove = itemView.findViewById(R.id.buttonRemove);
        }
    }

    public interface OnKeyRemoveListener {
        void onKeyRemove(int key) throws SQLException;
    }
}
