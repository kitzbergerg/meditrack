package ase.meditrack.model.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
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
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.proxy.HibernateProxy;
import org.keycloak.representations.idm.UserRepresentation;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "users")
@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
public class User {

    @Id
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(nullable = false)
    private Float workingHoursPercentage;

    @Column(nullable = false)
    private Integer currentOverTime;


    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

    @OneToMany(mappedBy = "user")
    private List<Holiday> holidays;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, optional = false)
    @PrimaryKeyJoinColumn
    private Preferences preferences;

    @OneToMany(mappedBy = "swapRequestingUser")
    private List<ShiftSwap> requestedShiftSwaps;

    @OneToMany(mappedBy = "swapSuggestingUser")
    private List<ShiftSwap> suggestedShiftSwaps;

    @ManyToMany(mappedBy = "users")
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

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MonthlyWorkDetails> monthlyWorkDetails;


    public void addCanWorkShiftTypes(ShiftType shiftType) {
        if (canWorkShiftTypes == null) {
            canWorkShiftTypes = new ArrayList<>();
        }
        canWorkShiftTypes.add(shiftType);
    }

    public void addPreferredShiftTypes(ShiftType shiftType) {
        if (preferredShiftTypes == null) {
            preferredShiftTypes = new ArrayList<>();
        }
        preferredShiftTypes.add(shiftType);
    }

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
