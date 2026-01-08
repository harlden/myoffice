package com.example.myoffice.ui.services.classes;

import java.util.Arrays;
import java.util.Date;
import java.util.Objects;

public class MessageClass
{
    public static final int MAX_COMMENT_LENGTH = 256;
    public enum E_MESSAGE_TYPE
    {
        MESSAGE_TYPE_CHANGE_CONTRACT,
        MESSAGE_TYPE_RENEWAL_CONTRACT,
        MESSAGE_TYPE_FINISH_CONTRACT,
        MESSAGE_TYPE_OTHER,
        MESSAGE_TYPE_COUNT;
    };

    private static final int MAX_MESSAGE_TYPE = MessageClass.E_MESSAGE_TYPE.values().length;
    public static String messages_name[] = {"Изменение контракта", "Продление контракта", "Завершение контракта", "Другое"};

    private int messageid, message_type, accountid, send_accountid, is_read;
    private String title, message, send_full_name;
    private Date send_date;

    public MessageClass(int messageid, int message_type, int accountid, int send_accountid, int is_read, String title, String message, String send_full_name, Date send_date) {
        this.messageid = messageid;
        this.message_type = message_type;
        this.accountid = accountid;
        this.send_accountid = send_accountid;
        this.is_read = is_read;
        this.title = title;
        this.message = message;
        this.send_date = send_date;
        this.send_full_name = send_full_name;
    }

    public int getMessageID() { return messageid; }
    public String getMessageName()
    {
        return messages_name[message_type];
    }
    public static String getStatementNameOfType(int statementType) { return messages_name[statementType]; }
    public int getMessageIntType() { return message_type; }
    public static int getMessageIntOfName(String name) {
        return Arrays.asList(messages_name).indexOf(name);
    }

    public int getAccountID() { return accountid; }
    public int getSendAccountID() { return send_accountid; }
    public String getSendFullName() { return send_full_name; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public Date getSendDate() { return send_date; }
    public int isRead() { return is_read; }

    public static boolean isValidMessageName(String statement_name) {
        return Arrays.asList(messages_name).contains(statement_name);
    }
}
