package account.validators;

import account.models.User;
import account.models.UserResponse;
import account.repos.UserRepository;
import account.util.UserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Component
public class AccountValidator implements Validator {
    UserRepository userRepository;
    @Autowired
    public AccountValidator(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {

    }
}
