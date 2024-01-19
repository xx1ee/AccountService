package account.util;

import org.springframework.http.HttpStatus;


public class UserCreateException extends RuntimeException {
    private HttpStatus status;
    private String error;
    private String path;

    public UserCreateException(HttpStatus status, String error, String path) {
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public HttpStatus getStatus() {
        return this.status;
    }


    public String getError() {
        return this.error;
    }

    public String getPath() {
        return this.path;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public void setError(String error) {
        this.error = error;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
