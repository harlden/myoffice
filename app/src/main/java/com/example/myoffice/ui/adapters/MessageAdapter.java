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
import com.example.myoffice.ui.services.classes.MessageClass;
import com.example.myoffice.ui.services.classes.StatementClass;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder>
{

    private List<MessageClass> messageClasses;
    private OnMessageClickListener listener;
    private static boolean is_hide_button;

    public MessageAdapter(List<MessageClass> statementClasses, boolean is_hide_button, OnMessageClickListener listener) {
        this.messageClasses = statementClasses;
        this.listener = listener;
        this.is_hide_button = is_hide_button;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MessageClass messageClass = messageClasses.get(position);
        holder.messageType.setText(messageClass.getMessageName());
        holder.messageSendFio.setText(messageClass.getSendFullName());
        holder.messageSendDate.setText(messageClass.getSendDate().toString());

        holder.viewButton.setOnClickListener(v -> listener.onViewClick(messageClass));

        if(is_hide_button)
        {
            holder.viewButton.setVisibility(View.INVISIBLE);
        }

        if(messageClass.isRead() == 1)
        {
            holder.unreadButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return messageClasses.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView messageType;
        TextView messageSendFio;
        TextView messageSendDate;
        ImageView viewButton, unreadButton;
        LinearLayout containerStatement; // Добавьте это

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            messageType = itemView.findViewById(R.id.messageType);
            messageSendFio = itemView.findViewById(R.id.messageSendFio);
            messageSendDate = itemView.findViewById(R.id.messageSendDate);
            viewButton = itemView.findViewById(R.id.message_button_view);
            unreadButton = itemView.findViewById(R.id.message_button_unread);
            containerStatement = itemView.findViewById(R.id.ContainerStatement); // Инициализация
        }
    }

    public interface OnMessageClickListener {
        void onViewClick(MessageClass message);
    }

    public void updateList(List<MessageClass> newList) {
        messageClasses.clear();
        messageClasses.addAll(newList);
        notifyDataSetChanged();
    }
}
