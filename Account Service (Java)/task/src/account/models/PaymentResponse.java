package account.models;

import account.repos.PaymentRepository;
import account.repos.UserRepository;
import account.services.PaymentService;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentResponse {
    @JsonIgnore
    private final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};

    private String name;
    private String lastname;
    private String period;
    private String salary;

    public PaymentResponse(Payment payment, User user) {
        this.name = user.getName();
        this.lastname = user.getLastname();
        this.period = months[payment.getMonth() - 1] + "-" + payment.getYear();
        this.salary = (payment.getSalary() / 100) + " dollar(s) " + (payment.getSalary() % 100) + " cent(s)";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getSalary() {
        return salary;
    }

    public void setSalary(String salary) {
        this.salary = salary;
    }
}
