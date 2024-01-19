package account.models;

public class DeletedSuccessfully {
    String user;
    String status = "Deleted successfully!";

    public DeletedSuccessfully() {
    }

    public DeletedSuccessfully(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
