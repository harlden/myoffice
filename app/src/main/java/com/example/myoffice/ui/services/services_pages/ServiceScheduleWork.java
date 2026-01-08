package com.example.myoffice.ui.services.services_pages;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import com.example.myoffice.R;
import com.example.myoffice.databinding.ServiceScheduleWorkBinding;
import com.example.myoffice.user_data;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class ServiceScheduleWork {
    public static int MAX_SCHEDULE_WORK_DAY = 7;
    public static int MIN_SCHEDULE_WORK_DAY = 0;
    public MaterialCalendarView materialCalendarView;
    public Context context;

    public ServiceScheduleWork(Context context) {
        this.context = context;
    }

    @SuppressLint("ResourceAsColor")
    public boolean LoadCalendar(ServiceScheduleWorkBinding binding_schedule_work) {
        int workDays = (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_SCHEDULE_WORK_DAY_FIRST);
        int offDays = (Integer) user_data.GetUserData(user_data.E_USER_DATA.DATA_SCHEDULE_WORK_DAY_SECOND);

        materialCalendarView = binding_schedule_work.calendarView;
        materialCalendarView.removeDecorators();

        // Декоратор для рабочих дней (синие точки)
        materialCalendarView.addDecorator(new WorkDayDecorator(workDays, offDays));

        // Декоратор для выходных дней (красные точки)
        materialCalendarView.addDecorator(new DayOffDecorator(workDays, offDays));

        TextView calendarInfo = binding_schedule_work.calendarInfo;

        int workDayColor = ContextCompat.getColor(context, R.color.main_grey);
        int dayOffColor = ContextCompat.getColor(context, R.color.main_red);

        String scheduleText = String.format(Locale.US, "График работы %d через %d\n", workDays, offDays);

        SpannableStringBuilder builder = new SpannableStringBuilder(scheduleText);

        SpannableString workDay = new SpannableString("● Рабочий день\n");
        workDay.setSpan(new ForegroundColorSpan(workDayColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        workDay.setSpan(new RelativeSizeSpan(1.2f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        SpannableString dayOff = new SpannableString("● Выходной день");
        dayOff.setSpan(new ForegroundColorSpan(dayOffColor), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        dayOff.setSpan(new RelativeSizeSpan(1.2f), 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        builder.append(workDay).append(dayOff);

        calendarInfo.setText(builder);
        return true;
    }

    private abstract class ScheduleDecorator implements DayViewDecorator {
        protected final int workDays;
        protected final int offDays;
        protected final Calendar startDate;

        ScheduleDecorator(int workDays, int offDays) {
            this.workDays = workDays;
            this.offDays = offDays;
            this.startDate = getNearestMonday();
        }

        protected boolean isWorkDay(CalendarDay day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(day.getYear(), day.getMonth() - 1, day.getDay());

            long diff = TimeUnit.MILLISECONDS.toDays(
                    calendar.getTimeInMillis() - startDate.getTimeInMillis()
            );
            return (diff % (workDays + offDays)) < workDays;
        }

        private Calendar getNearestMonday() {
            Calendar monday = Calendar.getInstance();
            monday.set(2025, Calendar.JANUARY, 1); // Начальная дата

            while (monday.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
                monday.add(Calendar.DAY_OF_MONTH, 1);
            }

            return monday;
        }
    }

    private class WorkDayDecorator extends ScheduleDecorator {
        WorkDayDecorator(int workDays, int offDays) {
            super(workDays, offDays);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return isWorkDay(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // Синяя точка под цифрой
            view.addSpan(new DotSpan(8, ContextCompat.getColor(context, R.color.main_grey)));
        }
    }

    private class DayOffDecorator extends ScheduleDecorator {
        DayOffDecorator(int workDays, int offDays) {
            super(workDays, offDays);
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return !isWorkDay(day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            // Красная точка под цифрой
            view.addSpan(new DotSpan(8, ContextCompat.getColor(context, R.color.main_red)));
        }
    }
}