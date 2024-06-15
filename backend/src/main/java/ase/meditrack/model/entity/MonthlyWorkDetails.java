package ase.meditrack.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity(name = "monthly_work_details")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class MonthlyWorkDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private Integer month;

    private Integer year;

    private float hoursShouldWork;

    private float hoursActuallyWorked;

    private Integer overtime;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "monthly_plan_id", nullable = false)
    private MonthlyPlan monthlyPlan;

}
