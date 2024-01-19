package account.validators;

import account.dto.GrantUser;
import account.models.Role;
import account.services.UserService;
import account.util.UserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.util.List;

@Component
public class GrantUserValidator implements Validator {
    private final UserService userService;
    private final List<String> roles = List.of("USER", "ACCOUNTANT", "ADMINISTRATOR", "AUDITOR");
    private final List<String> operations = List.of("GRANT", "REMOVE");

    @Autowired
    public GrantUserValidator(UserService userService) {
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return GrantUser.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        var u = (GrantUser) target;
        if (userService.findByEmail(u.getUser()) == null) {
            throw new UserExistException(HttpStatus.NOT_FOUND, "User not found!","Not Found", "/api/admin/user/role");
        }
        if (!roles.contains(u.getRole())) {
            throw new UserExistException(HttpStatus.NOT_FOUND, "Role not found!", "Not Found", "/api/admin/user/role");
        }
        if (u.getOperation().equals("REMOVE")) {
            if (u.getRole().equals("ADMINISTRATOR")) {
                throw new UserExistException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!", "Bad Request", "/api/admin/user/role");
            }
            var userRemoveRole = userService.findByEmail(u.getUser());
            boolean t = false;
            for (Role g : userRemoveRole.authorities) {
                if (g.getAuthority().equals("ROLE_" + u.getRole())) {
                    if (userRemoveRole.authorities.size() == 1) {
                        throw new UserExistException(HttpStatus.BAD_REQUEST, "The user must have at least one role!", "Bad Request", "/api/admin/user/role");
                    }
                    t = true;
                    break;
                }
            }
            if (t == false) {
                throw new UserExistException(HttpStatus.BAD_REQUEST, "The user does not have a role!", "Bad Request", "/api/admin/user/role");
            }
        }
        if (u.getOperation().equals("GRANT") && u.getRole().equals("ADMINISTRATOR")) {
            var userGranRole = userService.findByEmail(u.getUser());
            for (Role g : userGranRole.authorities) {
                if (g.getAuthority().equals("ROLE_USER") || g.getAuthority().equals("ROLE_ACCOUNTANT") || u.getRole().equals("ROLE_AUDITOR")) {
                    throw new UserExistException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!", "Bad Request", "/api/admin/user/role");
                }
            }
        }
        if (u.getOperation().equals("GRANT") && (u.getRole().equals("ACCOUNTANT") || u.getRole().equals("USER")|| u.getRole().equals("AUDITOR"))) {
            var userGranRole = userService.findByEmail(u.getUser());
            for (Role g : userGranRole.authorities) {
                if (g.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                    throw new UserExistException(HttpStatus.BAD_REQUEST, "The user cannot combine administrative and business roles!", "Bad Request", "/api/admin/user/role");
                }
            }
        }
    }
}
