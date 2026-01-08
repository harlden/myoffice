package com.example.myoffice.ui.services.classes;

import java.util.Arrays;

public class ActionEmployer
{
    public enum E_ACTIONS_EMPLOYER_TYPE
    {
        ACTION_EMPLOYER_TYPE_ENCOURAGEMENT,
        ACTION_EMPLOYER_TYPE_PUNISHMENT,
        ACTION_EMPLOYER_TYPE_COUNT
    };
    public static int MAX_ACTIONS_EMPLOYER = E_ACTIONS_EMPLOYER_TYPE.ACTION_EMPLOYER_TYPE_COUNT.ordinal();

    public static String actions_employers_name[] = {"Поощрение", "Наказание"};

    private int actionid, accountid, action_type;
    private String name, order;
    private String surname;
    private String date;
    private String comment;

    public ActionEmployer(int actionid, int accountid, int action_type, String order, String date, String name, String surname, String comment)
    {
        this.actionid = actionid;
        this.accountid = accountid;
        this.name = name;
        this.surname = surname;
        this.action_type = action_type;
        this.order = order;
        this.date = date;
        this.comment = comment;
    }

    public String getFullName() {
        return surname + " " + name;
    }

    public String getName() {
        return name;
    }
    public String getDate() { return date; }
    public String getSurnameEmployer() { return surname; }
    public String getNameEmployer() { return name; }
    public int getAccountID() { return accountid; }
    public int getActionType() { return action_type; }
    public String getOrder() { return order; }
    public String getActionTypeName(int action_type) { return actions_employers_name[action_type]; }
    public static int getActionType(String action_name) {
        return Arrays.asList(actions_employers_name).indexOf(action_name);
    }
    public int getActionID() { return actionid;}
    public String getComment() { return comment; }
}
