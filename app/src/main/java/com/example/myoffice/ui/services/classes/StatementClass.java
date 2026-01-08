package com.example.myoffice.ui.services.classes;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class StatementClass
{
    public static final int MAX_COMMENT_LENGTH = 32;
    public enum E_STATEMENT_TYPE
    {
        STATEMENT_TYPE_VACATION,
        STATEMENT_TYPE_DAY_OFF,
        STATEMENT_TYPE_COUNT;
    };

    private static final int MAX_STATEMENT_TYPE = StatementClass.E_STATEMENT_TYPE.values().length;
    public static String statements_name[] = {"Отпуск", "Отгул за свой счёт"};

    private int statementID, statementType, is_accept;
    private String fullName, comment, token_notification;
    private Date startDate, endDate, createDate;

    public StatementClass(int statementID, int statementType, String fullName, Date startDate, Date endDate, String comment,
                          Date createDate, int is_accept, String token_notification) {
        this.statementID = statementID;
        this.statementType = statementType;
        this.fullName = fullName;
        this.startDate = startDate;
        this.endDate = endDate;
        this.comment = comment;
        this.createDate = createDate;
        this.is_accept = is_accept;
        this.token_notification = token_notification;
    }

    public int getStatementID() { return statementID; }
    public String getStatementName()
    {
        return statements_name[statementType];
    }
    public static String getStatementNameOfType(int statementType) { return statements_name[statementType]; }
    public int getStatementIntType() { return statementType; }
    public static int getStatementIntOfName(String name) {
        return Arrays.asList(statements_name).indexOf(name);
    }

    public String getFullName() {
        return fullName;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }
    public Date getCreateDate() { return createDate; }

    public String getComment() {
        return comment;
    }
    public String getTokenNotification() { return token_notification; }
    public boolean isAccept() { return (is_accept != 0); }
    public static boolean isValidStatementName(String statement_name) {
        return Arrays.asList(statements_name).contains(statement_name);
    }

    public static int isOwnExpense(String name)
    {
        if(Objects.equals(name, "Отгул за свой счёт"))
            return 1;

        return 0;
    }
}
