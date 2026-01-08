package com.example.myoffice.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myoffice.EditTextItem;
import com.example.myoffice.R;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;

import java.util.List;
import java.util.Objects;

public class EditTextAndSpinnerAdapter extends RecyclerView.Adapter<EditTextAndSpinnerAdapter.ViewHolder> {

    private List<EditTextItem> items;
    private RecyclerView recyclerView; // Добавьте ссылку на RecyclerView
    private Context context;

    public EditTextAndSpinnerAdapter(RecyclerView recyclerView, List<EditTextItem> items, Context context) {
        this.recyclerView = recyclerView; // Инициализируем ссылку
        this.items = items;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edittext_spinner, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        EditTextItem item = items.get(position);

        // Устанавливаем текст описания
        holder.label.setText(item.getDescription()); // Используем hint как описание

        if (item.isSpinner())
        {
            holder.editText.setVisibility(View.GONE);
            holder.spinner.setVisibility(View.VISIBLE);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(holder.itemView.getContext(), android.R.layout.simple_spinner_item, item.getOptions());
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            holder.spinner.setAdapter(adapter);
            holder.spinner.setSelection(item.getSelectItemSpinner());
        }
        else
        {
            holder.editText.setVisibility(View.VISIBLE);
            holder.spinner.setVisibility(View.GONE);
            holder.editText.setHint(item.getHint());
            holder.editText.setText(item.getDefaultText());
            holder.editText.setEnabled(!item.isClose());

            if(item.isDate())
            {
                holder.editText.setFocusable(false);
                holder.editText.setOnClickListener(v -> ServiceAdditional.showDatePickerDialog(context, holder.editText));
            }
        }
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public String getInputData(int position) {
        EditTextItem item = items.get(position);
        if (item.isSpinner()) {
            return (String) ((ViewHolder) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position))).spinner.getSelectedItem();
        } else {
            return ((ViewHolder) Objects.requireNonNull(recyclerView.findViewHolderForAdapterPosition(position))).editText.getText().toString();
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        Spinner spinner;
        TextView label;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label);
            editText = itemView.findViewById(R.id.editText);
            spinner = itemView.findViewById(R.id.spinner);
        }
    }
}