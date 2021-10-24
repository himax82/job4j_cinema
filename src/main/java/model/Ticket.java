package model;

import java.sql.Timestamp;
import java.util.Objects;

public class Ticket {
    private int id;
    private final int sessionId;
    private final int row;
    private final int cell;
    private final int accountId;

    public Ticket(int id, int sessionId, int row, int cell, int accountId) {
        this.id = id;
        this.sessionId = sessionId;
        this.row = row;
        this.cell = cell;
        this.accountId = accountId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public int getSessionId() {
        return sessionId;
    }

    public int getRow() {
        return row;
    }

    public int getCell() {
        return cell;
    }

    public int getAccountId() {
        return accountId;
    }
}
