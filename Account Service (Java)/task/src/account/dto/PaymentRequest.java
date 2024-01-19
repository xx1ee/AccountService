package account.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class PaymentRequest {
    @JsonIgnore
    private final String[] months = {"January", "February", "March", "April", "May", "June",
            "July", "August", "September", "October", "November", "December"};
    @Pattern(regexp = "^(0[1-9]|1[0-2])-(\\d{4})$", message = "Wrong date")
    String period;
    @Min(value = 0, message = "Salary must be non negative!")
    Integer salary;
    @NotNull
    @Pattern(regexp = "[A-Za-z0-9._%+-]+@acme.com")
    String employee;

    public PaymentRequest() {
    }

    public PaymentRequest(String period, Integer salary, String employee) {
        this.period = period;
        this.salary = salary;
        this.employee = employee;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public String getEmployee() {
        return employee;
    }

    public void setEmployee(String employee) {
        this.employee = employee;
    }
    public String mapPeriodToMonthWordsAndYear() {
        return  months[Integer.parseInt(getPeriod().split("-")[0]) - 1] + "-" + Integer.parseInt(getPeriod().split("-")[1]);
    }
}
