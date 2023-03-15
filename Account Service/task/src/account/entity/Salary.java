package account.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.Pattern;

@Entity
@Table(name = "salary")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Salary {
    @Id
    @Column(name = "id", nullable = false)
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String employee;
    @Pattern(regexp = "^(0[1-9]|1[012])-(19|20)\\d\\d$", message = "Period must be in MM/YYYY format")
    private String period;
    @Min(value = 0, message = "Salary must be positive")
    private Long salary;
}