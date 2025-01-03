package account.repo;

import account.entity.Salary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;

@Repository
public interface SalaryRepository extends JpaRepository<Salary, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE Salary s SET s.salary = ?1 WHERE s.period = ?2 and s.employee = ?3")
    void updateSalaryByPeriod(Long salary, String period, String employee);

}
