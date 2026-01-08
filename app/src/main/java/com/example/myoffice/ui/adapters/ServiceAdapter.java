package com.example.myoffice.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.myoffice.R;
import com.example.myoffice.ui.services.classes.ServiceItem;

import java.util.List;

public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {
    private List<ServiceItem> services;
    private OnServiceClickListener listener;

    public interface OnServiceClickListener  {
        void onServiceClick(ServiceItem service);
    }

    public ServiceAdapter(List<ServiceItem> services, OnServiceClickListener  listener) {
        this.services = services;
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ServiceItem service = services.get(position);
        holder.title.setText(service.getTitle());
        holder.icon.setImageResource(service.getIconResId());
        holder.description.setText(service.getDescription());

        holder.itemView.setOnClickListener(v -> listener.onServiceClick(service));
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description;
        ImageView icon;

        public ViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.service_title);
            icon = itemView.findViewById(R.id.service_icon);
            description = itemView.findViewById(R.id.service_description);
        }
    }
}