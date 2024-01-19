package account.controllers;

import account.models.*;
import account.repos.RoleRepository;
import account.repos.UserRepository;
import account.services.PaymentService;
import account.services.SecurityEventService;
import account.services.UserService;
import account.util.NewException;
import account.util.UserCreateException;
import account.util.UserExistException;
import account.validators.AccountValidator;

import account.validators.NewPasswordValidator;
import account.validators.PaymentValidator;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api")
public class AccountController {
    private final AccountValidator validator;
    private final NewPasswordValidator newPasswordValidator;
    private final PaymentValidator paymentValidator;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserService userService;
    private final PaymentService paymentService;
    private final SecurityEventService securityEventService;
    private final String periodRegexp = "((0[1-9])|(1[0-2]))-[0-9]{4}";

    @Autowired
    public AccountController(AccountValidator validator, UserRepository userRepository, UserService userService, RoleRepository roleRepository,
                             NewPasswordValidator newPasswordValidator, PaymentValidator paymentValidator,
                             PaymentService paymentService, SecurityEventService securityEventService) {
        this.validator = validator;
        this.userRepository = userRepository;
        this.userService = userService;
        this.roleRepository = roleRepository;
        this.newPasswordValidator = newPasswordValidator;
        this.paymentValidator = paymentValidator;
        this.paymentService = paymentService;
        this.securityEventService = securityEventService;
    }

    @PostMapping("/auth/signup")
    public UserResponse signUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        validator.validate(user, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new UserCreateException(HttpStatus.BAD_REQUEST, "Bad Request", "/api/auth/signup");
        } else {
            securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.CREATE_USER), "Anonymous", user.getEmail().toLowerCase(), "/api/auth/signup" ));
            return userService.register(user);
        }
    }
    @GetMapping("/security/events/")
    public List<SecurityEvent> audit(@AuthenticationPrincipal User user) {
        return securityEventService.findAll();
    }
    @PostMapping("/auth/changepass")
    public UserUpdatedPasswordResponse changePass(@AuthenticationPrincipal User principal, @RequestBody @Valid NewPassword newPassword, BindingResult bindingResult) {
        newPasswordValidator.validate(newPassword, bindingResult);
        if (bindingResult.hasErrors()) {
            for (var result : bindingResult.getFieldErrors()) {
                if (result.getField().equals("new_password")) {
                    throw new UserExistException(HttpStatus.BAD_REQUEST, "Password length must be 12 chars minimum!",
                            "Bad Request", "/api/auth/changepass");
                }
            }

        }
        securityEventService.save(new SecurityEvent(String.valueOf(System.currentTimeMillis()),String.valueOf(SecurityAction.CHANGE_PASSWORD), principal.getEmail().toLowerCase(), principal.getEmail().toLowerCase(), "/api/auth/changepass" ));
        return userService.changePass(principal, newPassword.getNew_password());
    }
    @PostMapping("/acct/payments")
    public StatusResponse postPayment(@RequestBody List<PaymentRequest> paymentList) {
        StringBuilder stringBuilderErrors = new StringBuilder();
        for (PaymentRequest p : paymentList) {
            if (!p.getEmployee().matches("[A-Za-z0-9._%+-]+@acme.com")) {
                stringBuilderErrors
                        .append("payments[")
                        .append("].")
                        .append("employee")
                        .append(":")
                        .append("Wrong email!")
                        .append("; ");
            }
            if (!p.getPeriod().matches("^(0[1-9]|1[0-2])-(\\d{4})$")) {
                stringBuilderErrors
                        .append("payments[")
                        .append("].")
                        .append("period")
                        .append(":")
                        .append("Wrong date!")
                        .append("; ");
            }
            if (p.getSalary() < 0) {
                stringBuilderErrors
                        .append("payments[")
                        .append("].")
                        .append("salary")
                        .append(":")
                        .append("Value must be > 0")
                        .append("; ");
            }
            if (userService.findByEmail(p.getEmployee()) == null) {
                stringBuilderErrors
                        .append("payments[")
                        .append("].")
                        .append("employee")
                        .append(":")
                        .append("This employee is not a user!")
                        .append("; ");
            } else {
                var payments = paymentService.findPaymentsByEmail(p.getEmployee());
                for (PaymentResponse pay : payments) {
                    if (pay.getPeriod().equals(p.getPeriod())) {
                        stringBuilderErrors
                                .append("payments[")
                                .append("].")
                                .append("period")
                                .append(":")
                                .append("Period must be unique!")
                                .append("; ");
                    }
                }
            }
            if (!stringBuilderErrors.toString().isEmpty()) {
                throw new UserExistException(HttpStatus.BAD_REQUEST, stringBuilderErrors.toString(), "Bad Request", "/api/acct/payments");
            }
        }
        for (PaymentRequest p : paymentList) {
            paymentService.save(p);
        }
        return new StatusResponse();
    }
    @PutMapping("/acct/payments")
    public StatusResponse putPayment(@RequestBody PaymentRequest p) {
        StringBuilder stringBuilderErrors = new StringBuilder();
        if (!p.getEmployee().matches("[A-Za-z0-9._%+-]+@acme.com")) {
            stringBuilderErrors
                    .append("payments[")
                    .append("].")
                    .append("employee")
                    .append(":")
                    .append("Wrong email!")
                    .append("; ");
        }
        if (!p.getPeriod().matches("^(0[1-9]|1[0-2])-(\\d{4})$")) {
            stringBuilderErrors
                    .append("payments[")
                    .append("].")
                    .append("period")
                    .append(":")
                    .append("Wrong date!")
                    .append("; ");
        }
        if (p.getSalary() < 0) {
            stringBuilderErrors
                    .append("payments[")
                    .append("].")
                    .append("salary")
                    .append(":")
                    .append("Value must be > 0")
                    .append("; ");
        }
        if (userService.findByEmail(p.getEmployee()) == null) {
            stringBuilderErrors
                    .append("payments[")
                    .append("].")
                    .append("employee")
                    .append(":")
                    .append("This employee is not a user!")
                    .append("; ");
        } else {
            var payments = paymentService.findPaymentsByEmail(p.getEmployee());
            for (PaymentResponse pay : payments) {
                if (pay.getPeriod().equals(p.getPeriod())) {
                    stringBuilderErrors
                            .append("payments[")
                            .append("].")
                            .append("period")
                            .append(":")
                            .append("Period must be unique!")
                            .append("; ");
                }
            }
        }
        if (!stringBuilderErrors.toString().isEmpty()) {
            throw new UserExistException(HttpStatus.BAD_REQUEST, stringBuilderErrors.toString(), "Bad Request", "/api/acct/payments");
        }
        paymentService.update(p);
        StatusResponse updateStatus = new StatusResponse();
        updateStatus.setStatus("Updated successfully!");
        return updateStatus;
    }
    @GetMapping("/empl/payment")
    public ResponseEntity<?> getPayment(@AuthenticationPrincipal User user, @RequestParam(required = false) String period) {
        if (period == null) {
            return new ResponseEntity<>(paymentService.findPaymentsByEmail(user.getEmail()), HttpStatus.OK);
        }
        if (!period.matches(periodRegexp)) {
            throw new UserExistException(HttpStatus.BAD_REQUEST, "Period has a wrong format!", "Bad Request", "/api/empl/payment");
        }
        if (paymentService.findPaymentByEmailAndPeriod(user.getEmail(), Integer.valueOf(period.split("-")[0]), Integer.valueOf(period.split("-")[1])) == null) {
            return ResponseEntity.status(200).body(null);
        }
        return new ResponseEntity<>(paymentService.findPaymentByEmailAndPeriod(user.getEmail(), Integer.valueOf(period.split("-")[0]), Integer.valueOf(period.split("-")[1])), HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<UserExistResponse> handleException(UserExistException e) {
        return new ResponseEntity<>(new UserExistResponse(System.currentTimeMillis(), e.getStatus().value(), e.getError(), e.getPath(), e.getMessage()), e.getStatus());
    }
    @ExceptionHandler
    private ResponseEntity<UserErrorResponse> handleException(UserCreateException e) {
        return new ResponseEntity<>(new UserErrorResponse(System.currentTimeMillis(),  400, e.getError(), e.getPath()), HttpStatus.BAD_REQUEST);
    }
}