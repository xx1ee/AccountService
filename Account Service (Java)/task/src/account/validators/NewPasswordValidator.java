package account.validators;

import account.models.NewPassword;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class NewPasswordValidator implements Validator {
    @Override
    public boolean supports(Class<?> clazz) {
        return NewPassword.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
