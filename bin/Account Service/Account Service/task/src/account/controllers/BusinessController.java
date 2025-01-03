package account.controllers;

import account.entity.Salary;
import account.services.SalaryService;
import account.services.UserDetailsServiceImpl;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api")
@Validated
public class BusinessController {

    UserDetailsServiceImpl userDetailsService;
    SalaryService salaryService;

    public BusinessController(UserDetailsServiceImpl userDetailsService, SalaryService salaryService) {
        this.userDetailsService = userDetailsService;
        this.salaryService = salaryService;
    }
    @GetMapping("/empl/payment")
    public Object getPayroll(@RequestParam(required = false) String period) {
        if (period == null) {
            return userDetailsService.getUserPayrolls(UserDetailsServiceImpl.getCurrentUser());
        }
        return userDetailsService.getSpecificUserPayroll(UserDetailsServiceImpl.getCurrentUser(), period);
    }

    @PostMapping("/acct/payments")
    public Map<String, String> uploadPayroll(@RequestBody List< @Valid Salary> salaries) {
        salaryService.sendPayroll(salaries);
        return Map.of("status", "Added successfully!");
    }

    @PutMapping("/acct/payments")
    public Map<String, String> updatePayroll(@RequestBody @Valid Salary salary) {
        salaryService.updatePayroll(salary);
        return Map.of("status", "Updated successfully!");
    }
}
