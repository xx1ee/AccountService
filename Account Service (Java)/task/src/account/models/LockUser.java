package account.models;

public class LockUser {
    String user;
    String operation;

    public LockUser() {
    }

    public LockUser(String user, String operation) {
        this.user = user;
        this.operation = operation;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }
}
