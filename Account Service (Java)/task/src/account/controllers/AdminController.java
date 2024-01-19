package account.controllers;

import account.dto.*;
import account.models.*;
import account.repos.RoleRepository;
import account.services.PaymentService;
import account.services.SecurityEventService;
import account.services.UserService;
import account.validators.GrantUserValidator;
import account.util.UserExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final UserService userService;
    private final PaymentService paymentService;
    private final RoleRepository roleRepository;
    private final GrantUserValidator grantUserValidator;
    private final SecurityEventService securityEventService;
    @Autowired
    public AdminController(UserService userService, RoleRepository roleRepository,
                           PaymentService paymentService, GrantUserValidator grantUserValidator, SecurityEventService securityEventService) {
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.paymentService = paymentService;
        this.grantUserValidator = grantUserValidator;
        this.securityEventService = securityEventService;
    }
    @PutMapping("/admin/user/access")
    public StatusResponse lockUnlock(@RequestBody LockUser lockUser, @AuthenticationPrincipal User admin) {
        StatusResponse statusResponse = new StatusResponse();
        if (userService.findByEmail(lockUser.getUser()) == null) {
            throw new UserExistException(HttpStatus.NOT_FOUND, "User not found!", "Not Found", "/api/admin/user/role");
        }
        var u =userService.findByEmail(lockUser.getUser());
        if (lockUser.getOperation().equals("LOCK")) {
            for (Role r : u.authorities) {
                if (r.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                    throw new UserExistException(HttpStatus.BAD_REQUEST, "Can't lock the ADMINISTRATOR!", "Bad Request", "/api/admin/user/access");
                }
            }
            userService.lock(u, "/api/admin/user/access");
            statusResponse.setStatus("User " + lockUser.getUser().toLowerCase() + " locked!");
            return statusResponse;
        }
        if (lockUser.getOperation().equals("UNLOCK")) {
            userService.unlock(u, admin);
            statusResponse.setStatus("User " + lockUser.getUser().toLowerCase() + " unlocked!");
            return statusResponse;
        }
        throw new UserExistException(HttpStatus.BAD_REQUEST, "Bad operation!", "Bad Request", "/api/admin/user/access");
    }
    @PutMapping("/admin/user/role")
    public UserResponse roleUser(@RequestBody GrantUser grantUser, BindingResult bindingResult, @AuthenticationPrincipal User admin) {
        grantUserValidator.validate(grantUser, bindingResult);
        if (!bindingResult.hasErrors()) {
            User response;
            Set<String> roles = new TreeSet<>();
            if (grantUser.getOperation().equals("GRANT")) {
                var user = userService.findByEmail(grantUser.getUser());
                var g = roleRepository.findFirstByAuthority("ROLE_" + grantUser.getRole());
                user.authorities.add(g);
                userService.put(user);
                response = userService.findByEmail(grantUser.getUser());
                for (Role r : response.authorities) {
                    roles.add(r.getAuthority());
                }
                securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.GRANT_ROLE), admin.getEmail().toLowerCase(), "Grant role " + grantUser.getRole() + " to " + user.getEmail().toLowerCase(), "/api/admin/user/role"));
            } else {
                var user = userService.findByEmail(grantUser.getUser());
                var g = roleRepository.findFirstByAuthority("ROLE_" + grantUser.getRole());
                Set<Role> newRoles = new HashSet<>();
                var oldRoles = user.authorities;
                for (Role r : oldRoles) {
                    if (!r.getAuthority().equals(g.getAuthority())) {
                        newRoles.add(r);
                    }
                }
                user.setAuthorities(newRoles);
                userService.put(user);

                response = userService.findByEmail(grantUser.getUser());
                for (Role r : response.authorities) {
                    roles.add(r.getAuthority());
                }
                securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.REMOVE_ROLE), admin.getEmail().toLowerCase(), "Remove role " + grantUser.getRole() + " from " + user.getEmail().toLowerCase(), "/api/admin/user/role"));
            }
            return new UserResponse(response.getId(), response.getName(), response.getLastname(), response.getEmail().toLowerCase(), roles);
        } else {
            throw new UserExistException(HttpStatus.NOT_FOUND, bindingResult.toString(), "Not Found", "/api/admin/user/role");
        }
    }
    @GetMapping("/admin/user/")
    public List<UserResponse> roleUser() {
        if (userService.findAllSortedById().isEmpty()) {
            return List.of();
        }
        return userService.findAllSortedById();
    }
    @DeleteMapping("/admin/user/{user}")
    public DeletedSuccessfully deleteUser(@PathVariable String user, @AuthenticationPrincipal User admin) {
        var u =userService.findByEmail(user);
        if (u== null) {
           throw new UserExistException(HttpStatus.NOT_FOUND, "User not found!", "Not Found", "/api/admin/user/" + user);
        }
        for (Role a : u.authorities) {
            if (a.getAuthority().equals("ROLE_ADMINISTRATOR")) {
                throw new UserExistException(HttpStatus.BAD_REQUEST, "Can't remove ADMINISTRATOR role!", "Bad Request", "/api/admin/user/" + user);
            }
        }
        securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.DELETE_USER), admin.getEmail().toLowerCase(), user.toLowerCase(), "/api/admin/user"));
        userService.deleteUser(user);
        return new DeletedSuccessfully(user);
    }
    @ExceptionHandler
    private ResponseEntity<UserExistResponse> handleException(UserExistException e) {
        return new ResponseEntity<>(new UserExistResponse(System.currentTimeMillis(), e.getStatus().value(), e.getError(), e.getPath(), e.getMessage()), e.getStatus());
    }
}
