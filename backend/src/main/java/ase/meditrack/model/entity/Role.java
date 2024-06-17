package ase.meditrack.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

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
@RequiredArgsConstructor
@AllArgsConstructor
public class Role {

    public Role(UUID id, String name, String color, String abbreviation, List<User> users, Team team,
                 List<ShiftType> shiftTypes) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.abbreviation = abbreviation;
        this.users = users;
        this.team = team;
        this.shiftTypes = shiftTypes;

        this.allowedFlextimeTotal = 1;
        this.allowedFlextimePerMonth = 1;
        this.daytimeRequiredPeople = 1;
        this.nighttimeRequiredPeople = 1;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String color;

    private String abbreviation;

    private Integer allowedFlextimeTotal;

    private Integer allowedFlextimePerMonth;

    private Integer daytimeRequiredPeople;

    private Integer nighttimeRequiredPeople;

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
