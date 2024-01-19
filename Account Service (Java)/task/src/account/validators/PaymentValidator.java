package account.validators;

import account.models.Payment;
import account.models.PaymentRequest;
import account.models.PaymentResponse;
import account.services.PaymentService;
import account.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
@Component
public class PaymentValidator implements Validator {
    private final PaymentService paymentService;
    private final UserService userService;

    @Autowired
    public PaymentValidator(PaymentService paymentService, UserService userService) {
        this.paymentService = paymentService;
        this.userService = userService;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return Payment.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        PaymentRequest payment = (PaymentRequest) target;
        System.out.println(payment.getEmployee() + payment.getSalary() + payment.getPeriod());
        if (payment.getSalary() < 0) {
            errors.rejectValue("salary", "", "Value must be > 0");
        }
        if (userService.findByEmail(payment.getEmployee()) == null) {
            errors.rejectValue("employee", "", "This employee is not a user!");
        } else {
            var payments = paymentService.findPaymentsByEmail(payment.getEmployee());
            for (PaymentResponse p : payments) {
                if (p.getPeriod().equals(payment.getPeriod())) {
                    errors.rejectValue("period", "", "Period must be unique!");
                }
            }
        }
    }
}
