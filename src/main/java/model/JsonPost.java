package model;

public class JsonPost {

    private int sessionId;
    private String place;
    private Account account;

    public JsonPost(int sessionId, String place, Account account) {
        this.sessionId = sessionId;
        this.place = place;
        this.account = account;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public Account getAccount() {
        return account;
    }

    public void setAccount(Account account) {
        this.account = account;
    }
}
