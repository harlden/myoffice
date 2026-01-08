package com.example.myoffice.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.EditTextItem;
import com.example.myoffice.R;

import java.util.List;

public class EditTextAdapter extends RecyclerView.Adapter<EditTextAdapter.EditTextViewHolder> {
    private List<EditTextItem> editTextItems;
    private RecyclerView recyclerView; // Ссылка на RecyclerView

    public EditTextAdapter(List<EditTextItem> editTextItems, RecyclerView recyclerView) {
        this.editTextItems = editTextItems;
        this.recyclerView = recyclerView; // Инициализация ссылки
    }

    @NonNull
    @Override
    public EditTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edittext, parent, false);
        return new EditTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EditTextViewHolder holder, int position) {
        EditTextItem item = editTextItems.get(position);
        holder.editText.setHint(item.getHint());
        holder.editText.setText(item.getDefaultText()); // Устанавливаем дефолтный текст
        holder.editText.setEnabled(!item.isClose()); // Устанавливаем возможность редактирования
    }

    @Override
    public int getItemCount() {
        return editTextItems.size();
    }

    public class EditTextViewHolder extends RecyclerView.ViewHolder {
        EditText editText;

        public EditTextViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
        }
    }

    // Новый метод для получения текущего текста из EditText
    public String getText(int position) {
        EditTextViewHolder holder = (EditTextViewHolder)
                recyclerView.findViewHolderForAdapterPosition(position);
        if (holder != null) {
            return holder.editText.getText().toString(); // Получаем текущий текст
        }
        return ""; // Если ViewHolder не найден, возвращаем пустую строку
    }
}