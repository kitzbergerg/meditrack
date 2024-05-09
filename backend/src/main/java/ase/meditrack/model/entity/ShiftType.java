package ase.meditrack.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "shift_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ShiftType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;

    private LocalTime startTime;

    private LocalTime endTime;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "shiftType")
    private List<Shift> shifts;

    @ManyToMany(mappedBy = "canWorkShiftTypes")
    private List<User> workUsers;

    @ManyToMany(mappedBy = "canWorkShiftTypes")
    private List<User> preferUsers;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ShiftType shiftType = (ShiftType) o;
        return getId() != null && Objects.equals(getId(), shiftType.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}