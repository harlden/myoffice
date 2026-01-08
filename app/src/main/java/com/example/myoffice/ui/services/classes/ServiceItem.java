package com.example.myoffice.ui.services.classes;

import com.example.myoffice.ui.services.fragments.Services;

public class ServiceItem {
    private String title;
    private String description;  // Добавлено
    private int iconResId;
    private Services.E_SERVICE_TYPE service_type;

    public ServiceItem(String title, String description, int iconResId, Services.E_SERVICE_TYPE service_type) {
        this.title = title;
        this.description = description;
        this.iconResId = iconResId;
        this.service_type = service_type;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconResId() {
        return iconResId;
    }

    public int GetServiceType()
    {
        return service_type.ordinal();
    }

    public static Services.E_SERVICE_TYPE getServiceTypeFromInt(int value)
    {
        if (value < 0 || value >= Services.E_SERVICE_TYPE.values().length) {
            throw new IllegalArgumentException("Неверное значение: " + value);
        }
        return Services.E_SERVICE_TYPE.values()[value];
    }
}