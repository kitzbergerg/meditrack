package ase.meditrack.model.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "shift")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class Shift {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "monthly_plan_id")
    private MonthlyPlan monthlyPlan;

    @ManyToOne
    @JoinColumn(name = "shift_type_id")
    private ShiftType shiftType;

    @ManyToMany(mappedBy = "shifts")
    private List<User> users;

    @ManyToMany(mappedBy = "suggestedShifts")
    private List<ShiftSwap> suggestedShiftSwaps;

    @OneToOne(mappedBy = "requestedShift", cascade = CascadeType.ALL)
    private ShiftSwap requestedShiftSwap;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Shift shift = (Shift) o;
        return getId() != null && Objects.equals(getId(), shift.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
