package account.models;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

public class NewPassword {
    @Size(min = 12, message = "Password length must be 12 chars minimum!")
    private String new_password;
    public NewPassword(String new_password) {
        this.new_password = new_password;
    }

    public NewPassword() {
    }

    public void setNew_password(String new_password) {
        this.new_password = new_password;
    }

    public String getNew_password() {
        return new_password;
    }
}
