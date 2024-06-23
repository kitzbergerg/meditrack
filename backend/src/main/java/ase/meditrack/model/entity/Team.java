package ase.meditrack.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "team")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true)
    private String name;

    @OneToMany(mappedBy = "team")
    private List<Role> roles;

    @OneToMany(mappedBy = "team")
    private List<User> users;

    @Column(nullable = false)
    private Integer daytimeRequiredPeople;

    @Column(nullable = false)
    private Integer nighttimeRequiredPeople;

    @OneToMany(mappedBy = "team")
    private List<MonthlyPlan> monthlyPlans;

    @OneToMany(mappedBy = "team")
    private List<ShiftType> shiftTypes;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        Team team = (Team) o;
        return getId() != null && Objects.equals(getId(), team.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
