package com.example.myoffice.ui.services.classes;

import java.sql.Time;
import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class LateClass
{
    public static final int MAX_COMMENT_LENGTH = 32;

    private int lateid, accountid;
    private String fullName, comment;
    private Date date_late;
    private Time arrival_time;

    public LateClass(int lateid, int accountid, String fullName, String comment, Date date_late, Time arrival_time) {
        this.lateid = lateid;
        this.accountid = accountid;
        this.fullName = fullName;
        this.comment = comment;
        this.date_late = date_late;
        this.arrival_time = arrival_time;
    }

    public int getLateID() { return lateid; }
    public int getAccountID() { return accountid; }
    public String getFullName() { return fullName; }
    public String getComment()
    {
        if(comment.isEmpty())
            return "Не указано";

        return comment; }
    public Date getDateLate() { return date_late; }
    public Time getArrivalTime() { return arrival_time; }
}
