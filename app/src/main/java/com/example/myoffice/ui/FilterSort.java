package com.example.myoffice.ui;

import android.content.Context;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;
import com.example.myoffice.R;
import com.example.myoffice.ui.adapters.LateAdapter;
import com.example.myoffice.ui.services.classes.LateClass;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.datepicker.MaterialDatePicker;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FilterSort {
    public enum SortOrder { ASC, DESC, ALPHABET }
    public enum DateFilter { ALL_TIME, CUSTOM }

    private final Context context;
    private final LateAdapter adapter;
    private List<LateClass> originalList;
    private List<LateClass> filteredList;

    private SortOrder currentSortOrder = SortOrder.ASC;
    private DateFilter currentDateFilter = DateFilter.ALL_TIME;
    private Calendar customStartDate;
    private Calendar customEndDate;

    public FilterSort(Context context, RecyclerView recyclerView, LateAdapter adapter) {
        this.context = context;
        this.adapter = adapter;
    }

    public void initialize(List<LateClass> originalList) {
        this.originalList = originalList;
        this.filteredList = new ArrayList<>(originalList);
        applyFilters();
    }

    public void setupDateFilters(ChipGroup dateFilterGroup) {
        // Устанавливаем первый чип (Все время) выбранным по умолчанию
        ((Chip)dateFilterGroup.getChildAt(0)).setChecked(true);

        dateFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // Если сняли выбор, возвращаем выбор на "Все время"
                ((Chip)group.getChildAt(0)).setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_all_time) {
                currentDateFilter = DateFilter.ALL_TIME;
                customStartDate = null;
                customEndDate = null;
            } else if (checkedId == R.id.chip_custom_date) {
                currentDateFilter = DateFilter.CUSTOM;
                showDateRangePicker(dateFilterGroup);
                return;
            }
            applyFilters();
        });
    }

    public void setupOrderFilters(ChipGroup orderFilterGroup) {
        // Устанавливаем первый чип (По возрастанию) выбранным по умолчанию
        ((Chip)orderFilterGroup.getChildAt(0)).setChecked(true);

        orderFilterGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (checkedIds.isEmpty()) {
                // Если сняли выбор, возвращаем выбор на первый чип
                ((Chip)group.getChildAt(0)).setChecked(true);
                return;
            }

            int checkedId = checkedIds.get(0);
            if (checkedId == R.id.chip_asc) {
                currentSortOrder = SortOrder.ASC;
            } else if (checkedId == R.id.chip_desc) {
                currentSortOrder = SortOrder.DESC;
            } else if (checkedId == R.id.chip_alphabet) {
                currentSortOrder = SortOrder.ALPHABET;
            }
            applyFilters();
        });
    }

    private void showDateRangePicker(ChipGroup dateFilterGroup) {
        MaterialDatePicker<Pair<Long, Long>> picker = MaterialDatePicker.Builder
                .dateRangePicker()
                .setTitleText("Выберите диапазон дат")
                .build();

        picker.addOnPositiveButtonClickListener(selection -> {
            customStartDate = Calendar.getInstance();
            customStartDate.setTimeInMillis(selection.first);
            customStartDate.set(Calendar.HOUR_OF_DAY, 0);
            customStartDate.set(Calendar.MINUTE, 0);
            customStartDate.set(Calendar.SECOND, 0);

            customEndDate = Calendar.getInstance();
            customEndDate.setTimeInMillis(selection.second);
            customEndDate.set(Calendar.HOUR_OF_DAY, 23);
            customEndDate.set(Calendar.MINUTE, 59);
            customEndDate.set(Calendar.SECOND, 59);

            applyFilters();
        });

        picker.addOnNegativeButtonClickListener(dialog -> {
            ((Chip)dateFilterGroup.getChildAt(0)).setChecked(true);
        });

        picker.addOnCancelListener(dialog -> {
            ((Chip)dateFilterGroup.getChildAt(0)).setChecked(true);
        });

        picker.show(((AppCompatActivity)context).getSupportFragmentManager(), picker.toString());
    }

    private void applyFilters() {
        filteredList = filterByDate(originalList);
        sortList(filteredList);
        adapter.updateList(filteredList);
    }

    private List<LateClass> filterByDate(List<LateClass> list) {
        List<LateClass> result = new ArrayList<>();

        // Получаем текущую дату без времени
        Calendar today = getCleanCalendar();

        for (LateClass item : list) {
            Calendar itemDate = getCleanCalendar();
            itemDate.setTime(item.getDateLate());

            switch (currentDateFilter) {
                case ALL_TIME:
                    if (customStartDate == null || isDateInRange(itemDate, customStartDate, customEndDate)) {
                        result.add(item);
                    }
                    break;

                case CUSTOM:
                    if (customStartDate != null && customEndDate != null &&
                            isDateInRange(itemDate, customStartDate, customEndDate)) {
                        result.add(item);
                    }
                    break;
            }
        }
        return result;
    }

    // Вспомогательный метод для получения "чистой" даты (без времени)
    private Calendar getCleanCalendar() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal;
    }

    // Проверка, что дата находится в диапазоне (включительно)
    private boolean isDateInRange(Calendar date, Calendar start, Calendar end) {
        return !date.before(start) && !date.after(end);
    }

    // Проверка, что две даты совпадают (день, месяц, год)
    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
                cal1.get(Calendar.DAY_OF_MONTH) == cal2.get(Calendar.DAY_OF_MONTH);
    }

    private void sortList(List<LateClass> list) {
        switch (currentSortOrder) {
            case ASC:
                Collections.sort(list, Comparator.comparing(LateClass::getDateLate));
                break;
            case DESC:
                Collections.sort(list, (o1, o2) -> o2.getDateLate().compareTo(o1.getDateLate()));
                break;
            case ALPHABET:
                Collections.sort(list, (o1, o2) ->
                        o1.getFullName().compareToIgnoreCase(o2.getFullName()));
                break;
        }
    }
}