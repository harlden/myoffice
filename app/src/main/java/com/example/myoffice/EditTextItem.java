package com.example.myoffice;

import java.util.List;

public class EditTextItem {
    private String hint, description;
    private String defaultText;
    private boolean isClose;
    private boolean isSpinner; // Поле для определения типа элемента
    private boolean isDate;
    private int select_item_spinner;
    private List<String> options; // Опции для спиннера

    // Конструктор для EditText
    public EditTextItem(String description, String hint, String defaultText, boolean isClose, boolean isDate) {
        this.hint = hint;
        this.description = description;
        this.defaultText = defaultText;
        this.isClose = isClose;
        this.isSpinner = false; // По умолчанию не спиннер
        this.isDate = isDate;
        this.select_item_spinner = 0;
    }

    // Конструктор для Spinner
    public EditTextItem(String description, String hint, List<String> options, boolean isClose, int select_item_spinner, boolean isDate) {
        this.hint = hint;
        this.description = description;
        this.defaultText = null; // Для спиннера нет значения по умолчанию
        this.isClose = isClose;
        this.isDate = isDate;
        this.isSpinner = true; // Это спиннер
        this.options = options;
        this.select_item_spinner = select_item_spinner;
    }

    public String getHint() {
        return hint;
    }

    public String getDescription()
    {
        return description;
    }

    public String getDefaultText() {
        return defaultText;
    }

    public boolean isClose() {
        return isClose;
    }
    public boolean isDate() { return isDate; }

    public boolean isSpinner() {
        return isSpinner;
    }

    public List<String> getOptions() {
        return options; // Получение опций для спиннера
    }

    public int getSelectItemSpinner() {
        return select_item_spinner;
    }
}