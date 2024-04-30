package ase.meditrack.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.proxy.HibernateProxy;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Entity(name = "shift_swap")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class ShiftSwap {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "requesting_user_id")
    private User swapRequestingUser;

    @ManyToMany
    @JoinTable(
            name = "shift_swap_user_suggestion",
            joinColumns = @JoinColumn(name = "shift_swap_id"),
            inverseJoinColumns = @JoinColumn(name = "users_id")
    )
    private List<User> swapSuggestingUsers;

    @OneToOne
    @JoinColumn(name = "requested_shift_id")
    private Shift requestedShift;

    @ManyToMany
    @JoinTable(
            name = "shift_swap_suggestion",
            joinColumns = @JoinColumn(name = "shift_swap_id"),
            inverseJoinColumns = @JoinColumn(name = "shift_id")
    )
    private List<Shift> suggestedShift;

    @Override
    public final boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null) return false;
        Class<?> oEffectiveClass = o instanceof HibernateProxy ? ((HibernateProxy) o)
                .getHibernateLazyInitializer().getPersistentClass() : o.getClass();
        Class<?> thisEffectiveClass = this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass() : this.getClass();
        if (thisEffectiveClass != oEffectiveClass) return false;
        ShiftSwap shiftSwap = (ShiftSwap) o;
        return getId() != null && Objects.equals(getId(), shiftSwap.getId());
    }

    @Override
    public final int hashCode() {
        return this instanceof HibernateProxy ? ((HibernateProxy) this)
                .getHibernateLazyInitializer().getPersistentClass().hashCode() : getClass().hashCode();
    }
}
