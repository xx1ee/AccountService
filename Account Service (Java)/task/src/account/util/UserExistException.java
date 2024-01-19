package account.util;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

public class UserExistException extends RuntimeException {
    private HttpStatus status;
    private String message;
    private String error;
    private String path;

    public UserExistException() {
    }

    public UserExistException(HttpStatus status, String message, String error, String path) {
        super(message);
        this.status = status;
        this.message = message;
        this.error = error;
        this.path = path;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
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

    public HttpStatus getStatus() {
        return status;
    }
    @Override
    public String getMessage() {
        return message;
    }
}
