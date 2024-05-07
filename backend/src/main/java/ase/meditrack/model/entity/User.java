package ase.meditrack.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Transient;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "users")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class User {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private Float workingHoursPercentage;

    private Integer currentOverTime;

    @ElementCollection
    @CollectionTable(name = "special_skills", joinColumns = @JoinColumn(name = "users_id"))
    private List<String> specialSkills;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "user")
    private List<Holiday> holidays;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private Preferences preferences;

    @OneToMany(mappedBy = "swapRequestingUser")
    private List<ShiftSwap> requestedShiftSwaps;

    @ManyToMany(mappedBy = "swapSuggestingUsers")
    private List<ShiftSwap> suggestedShiftSwaps;

    @ManyToMany
    @JoinTable(
            name = "user_shifts",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "shift_id")
    )
    private List<Shift> shifts;

    @ManyToMany
    @JoinTable(
            name = "user_can_work_shift_type",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "shift_type_id")
    )
    private List<ShiftType> canWorkShiftTypes;

    @ManyToMany
    @JoinTable(
            name = "user_prefers_shift_type",
            joinColumns = @JoinColumn(name = "users_id"),
            inverseJoinColumns = @JoinColumn(name = "shift_type_id")
    )
    private List<ShiftType> preferredShiftTypes;

    @Transient
    @JsonInclude
    private UserRepresentation userRepresentation;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        User user = (User) o;
        return getId() != null && Objects.equals(getId(), user.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
