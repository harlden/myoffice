package com.example.myoffice.ui.services.classes;

public class Employer {
    private Integer accountid, schedule_work_day_first, schedule_work_day_second;
    private String name;
    private String surname;
    private String job_title;
    private String date_invite, order_invite;

    public Employer(Integer accountid, String name, String surname, String job_title, String date_invite, String order_invite, Integer schedule_work_day_first,
                    Integer schedule_work_day_second) {
        this.accountid = accountid;
        this.name = name;
        this.surname = surname;
        this.job_title = job_title;
        this.date_invite = date_invite;
        this.order_invite = order_invite;
        this.schedule_work_day_first = schedule_work_day_first;
        this.schedule_work_day_second = schedule_work_day_second;
    }

    public String getFullName() {
        return surname + " " + name;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getJobTitle() {
        return job_title;
    }

    public Integer getAccountID()
    {
        return accountid;
    }
    public String getOrderInvite() { return order_invite; }
    public String getDateInvite() { return  date_invite; }
    public Integer getScheduleWorkDayFirst() { return schedule_work_day_first; }
    public Integer getScheduleWorkDaySecond() { return schedule_work_day_second; }
}
