package account.services;

import account.entity.Salary;
import account.errors.GenericException;
import account.errors.MoneyAlreadyAllocatedException;
import account.repo.SalaryRepository;
import account.repo.UserRepository;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
@Transactional
public class SalaryService {

    private final SalaryRepository salaryRepo;
    private final UserRepository userRepo;
    public SalaryService(SalaryRepository salaryRepo, UserRepository userRepo) {
        this.salaryRepo = salaryRepo;
        this.userRepo = userRepo;
    }

    public void sendPayroll(List<Salary> salaries) {
        salaries.forEach(
                salary -> userRepo.findByEmail(salary.getEmployee()).ifPresentOrElse(
                        user -> {
                            user.getSalaries().stream()
                                    .filter(salary1 -> salary1.getPeriod().equals(salary.getPeriod()))
                                    .findFirst()
                                    .ifPresent(salary1 -> {
                                        throw new MoneyAlreadyAllocatedException();
                                    });
                            salaryRepo.save(salary);
                            user.getSalaries().add(salary);
                            userRepo.save(user);
                            System.out.println(user);
                        },
                        () -> {
                            throw new GenericException("Employee not found");
                        }
                )

        );
    }

    public void updatePayroll(Salary salary) {
        userRepo.findByEmail(salary.getEmployee()).ifPresentOrElse(
                user -> salaryRepo.updateSalaryByPeriod(salary.getSalary(), salary.getPeriod(), salary.getEmployee()),
                () -> {
                    throw new GenericException("Employee not found");
                }
        );
    }

}
