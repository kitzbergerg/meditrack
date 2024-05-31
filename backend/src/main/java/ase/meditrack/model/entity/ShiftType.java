package ase.meditrack.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.time.LocalTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "shiftTypeNameAndTeamUnique", columnNames = {"name", "team_id"}),
                @UniqueConstraint(name = "shiftTypeColorAndTeamUnique", columnNames = {"color", "team_id"}),
                @UniqueConstraint(name = "shiftTypeAbbAndTeamUnique", columnNames = {"abbreviation", "team_id"})
        }
)
@Entity(name = "shift_type")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class ShiftType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private LocalTime breakStartTime;

    private LocalTime breakEndTime;

    @Pattern(regexp = "Day|Night", message = "Shift Type must be either 'Day' or 'Night'")
    private String type;

    private String color;

    private String abbreviation;

    @ManyToMany()
    @JoinTable(name = "shiftTypeRoles",
            joinColumns = @JoinColumn(name = "shiftType_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> requiredRoles;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "shiftType")
    private List<Shift> shifts;

    @ManyToMany(mappedBy = "canWorkShiftTypes")
    private List<User> workUsers;

    @ManyToMany(mappedBy = "preferredShiftTypes")
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
