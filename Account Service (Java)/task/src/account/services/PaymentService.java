package account.services;

import account.models.Payment;
import account.models.PaymentRequest;
import account.models.PaymentResponse;
import account.repos.PaymentRepository;
import account.repos.UserRepository;
import account.util.UserExistException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentService {
    private final PaymentRepository paymentRepository;
    private final UserRepository userRepository;
    @Autowired
    public PaymentService(PaymentRepository paymentRepository, UserRepository userRepository) {
        this.paymentRepository = paymentRepository;
        this.userRepository = userRepository;
    }
    @Transactional
    public void save(PaymentRequest payment) {
        StringBuilder stringBuilderErrors = new StringBuilder();
        List<PaymentResponse> payments = findPaymentsByEmail(payment.getEmployee());
        for (PaymentResponse pay : payments) {
            System.out.println(pay.getPeriod());
            System.out.println(payment.getPeriod());
            if (pay.getPeriod().equals(payment.mapPeriodToMonthWordsAndYear())) {
                stringBuilderErrors
                        .append("payments[")
                        .append("].")
                        .append("period")
                        .append(":")
                        .append("Period must be unique!")
                        .append("; ");
            }
        }
        if (stringBuilderErrors.toString().isEmpty()) {
            Payment paymentToSave = new Payment(Integer.valueOf(payment.getPeriod().split("-")[0]), Integer.valueOf(payment.getPeriod().split("-")[1]),
                    payment.getSalary(), payment.getEmployee());
            paymentRepository.save(paymentToSave);
        } else {
            throw new UserExistException(HttpStatus.BAD_REQUEST, stringBuilderErrors.toString(), "Bad Request", "/api/acct/payments");
        }
    }
    @Transactional
    public void update(PaymentRequest payment) {
        Payment paymentSet = paymentRepository.findPaymentByEmployeeIgnoreCaseAndMonthAndYear(payment.getEmployee(), Integer.valueOf(payment.getPeriod().split("-")[0]), Integer.valueOf(payment.getPeriod().split("-")[1]));
        paymentSet.setSalary(payment.getSalary());
        paymentRepository.save(paymentSet);
    }
    public List<PaymentResponse> findPaymentsByEmail(String email) {
        List<PaymentResponse> responses = new ArrayList<>();
        var payments = paymentRepository.findPaymentsByEmployeeIgnoreCaseOrderByMonthDescYearDesc(email);
        for (Payment p : payments) {
            responses.add(new PaymentResponse(p, userRepository.findByEmailIgnoreCase(email)));
        }
        return responses;
    }
    public PaymentResponse findPaymentByEmailAndPeriod(String email, Integer month, Integer year) {
        var p = paymentRepository.findPaymentByEmployeeIgnoreCaseAndMonthAndYear(email, month, year);
        if (p == null) {
            return null;
        } else {
            return new PaymentResponse(p, userRepository.findByEmailIgnoreCase(email));
        }
    }
}
