package ase.meditrack.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;
import java.util.Objects;
import java.util.UUID;


@Table(
        uniqueConstraints = {
                @UniqueConstraint(name = "roleNameAndTeamUnique", columnNames = {"name", "team_id"}),
                @UniqueConstraint(name = "roleColorAndTeamUnique", columnNames = {"color", "team_id"}),
                @UniqueConstraint(name = "roleAbbreviationAndTeamUnique", columnNames = {"abbreviation", "team_id"}),
        }
)
@Entity(name = "role")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String color;

    private String abbreviation;

    @OneToMany(mappedBy = "role")
    private List<User> users;

    @ManyToOne(optional = false)
    @JoinColumn(name = "team_id")
    private Team team;

    @ManyToMany(mappedBy = "requiredRoles")
    private List<ShiftType> shiftTypes;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Role role = (Role) o;
        return Objects.equals(id, role.id) && Objects.equals(name, role.name) && Objects.equals(users, role.users);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, users);
    }
}
