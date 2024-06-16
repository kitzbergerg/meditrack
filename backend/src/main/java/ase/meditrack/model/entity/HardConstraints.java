package ase.meditrack.model.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapKeyEnumerated;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "hard_constraints")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class HardConstraints {

    @Id
    private UUID id;

    Integer workingHours;

    Integer maxWeeklyHours;

    Integer maxConsecutiveShifts;

    Integer daytimeRequiredPeople;

    Integer nighttimeRequiredPeople;

    @OneToOne
    @MapsId
    private Team team;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        HardConstraints that = (HardConstraints) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
