package account.user;

public class Answer {
    private String email;
    private String status = "The password has been updated successfully";

    public Answer(String email) {
        this.email = email;
    }
    public Answer() {

    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getStatus() {
        return status;
    }

    public void setSuccess(String success) {
        this.status = success;
    }
}
