package account.repos;

import account.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findPaymentsByEmployeeIgnoreCaseOrderByMonthDescYearDesc(String email);
    Payment findPaymentByEmployeeIgnoreCaseAndMonthAndYear(String employee, Integer month, Integer year);

}
