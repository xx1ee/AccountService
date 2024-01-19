package account.dto;

public class StatusResponse {
    String status = "Added successfully!";

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public StatusResponse() {
    }
}
