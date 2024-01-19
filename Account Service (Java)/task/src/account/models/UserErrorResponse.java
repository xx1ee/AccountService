package account.models;

import org.springframework.http.HttpStatus;

public class UserErrorResponse {
    private Long timestamp;
    private Integer status;
    private String error;
    private String path;

    public UserErrorResponse(Long timestamp, Integer httpStatus, String error, String path) {
        this.timestamp = timestamp;
        this.status = httpStatus;
        this.error = error;
        this.path = path;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer httpStatus) {
        this.status = httpStatus;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }
}
