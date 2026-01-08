package com.example.myoffice;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.ui.adapters.EditTextAndSpinnerAdapter;
import com.example.myoffice.ui.services.fragments.ServiceAdditional;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class EditTextDialog {
    private Dialog dialog;
    private EditTextAndSpinnerAdapter adapter;
    private List<EditTextItem> items; // Список элементов
    private OnDataSubmitListener listener;

    public EditTextDialog(Context context, EditTextItem[] items, OnDataSubmitListener listener) {
        this.listener = listener;
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_layout);

        // Инициализация списка и адаптера
        this.items = new ArrayList<>();
        this.items.addAll(Arrays.asList(items));

        RecyclerView recyclerView = dialog.findViewById(R.id.recyclerView);
        adapter = new EditTextAndSpinnerAdapter(recyclerView, this.items, context); // Передаем список элементов
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        recyclerView.setAdapter(adapter);

        Button buttonOk = dialog.findViewById(R.id.buttonOk);
        buttonOk.setOnClickListener(v -> {
            Login.playClickSound(context, "click_tap");
            List<String> inputData = new ArrayList<>();
            boolean isClose = false;

            for (int i = 0; i < this.items.size(); i++) {
                inputData.add(adapter.getInputData(i)); // Получаем текущее значение
            }

            try {
                isClose = listener.onDataSubmit(inputData); // Передаем данные обратно
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

            if (isClose) {
                dialog.dismiss(); // Закрыть диалог
            }
        });

        Button buttonCancel = dialog.findViewById(R.id.buttonCancel);
        buttonCancel.setOnClickListener(v -> dialog.dismiss()); // Закрыть диалог
    }

    public void show() {
        dialog.show();
    }

    public interface OnDataSubmitListener {
        boolean onDataSubmit(List<String> data) throws SQLException;
    }
}