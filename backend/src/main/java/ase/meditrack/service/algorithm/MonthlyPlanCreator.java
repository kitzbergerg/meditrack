package ase.meditrack.service.algorithm;

import ase.meditrack.model.dto.SimpleRoleDto;
import ase.meditrack.model.dto.UserDto;
import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import ase.meditrack.model.mapper.RoleMapper;
import ase.meditrack.model.mapper.ShiftTypeMapper;
import ase.meditrack.model.mapper.UserMapper;
import ase.meditrack.repository.RoleRepository;
import ase.meditrack.repository.ShiftTypeRepository;
import ase.meditrack.service.RoleService;
import ase.meditrack.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class MonthlyPlanCreator {

    @Autowired
    UserService userService;
    @Autowired
    UserMapper userMapper;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    RoleMapper roleMapper;
    @Autowired
    ShiftTypeMapper shiftTypeMapper;
    @Autowired
    ShiftTypeRepository shiftTypeRepository;
    public MonthlyPlan createMonthlyPlan(int month, int year, UUID teamId) {

        // Create mock data
        // TODO: Replace mocked data with db fetch
        Team team = new Team();
        team.setId(teamId);
        team.setWorkingHours(40);
        team.setName("Mock Team");

        String[] cols = {"#571F4E", "#571F4E", "#4F759B", "#92C9B1"};

        List<ShiftType> shiftTypes = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            ShiftType shiftType = new ShiftType();
            shiftType.setId(UUID.randomUUID());
            shiftType.setName("Shift " + i);
            shiftType.setColor(cols[i%cols.length]);
            shiftType.setStartTime(LocalTime.of(9 + i * 3, 0));
            shiftType.setEndTime(LocalTime.of(12 + i * 3, 0));
            shiftTypes.add(shiftType);
        }

        List<Role> roles2 = roleRepository.findAll();

        List<User> users = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            UserDto user = new UserDto(
                    null,
                    UUID.randomUUID().toString(),
                    "s€cr€tPa$$w0rd",
                    UUID.randomUUID() + "@meditrack.com",
                    "Firstname" + i,
                    "Lastname" + i,
                    List.of("admin"),
                    roleMapper.toSimpleDto(roles2.get(i% roles2.size())),
                    100f-i,
                    0,
                    List.of("specialSkill1", "specialSkill2"),
                    null,
                    null,
                    null,
                    null,
                    null,
                    null,
                    new ArrayList<UUID>(),
                    null
            );
            User userEntity = userMapper.fromDto(user);
            users.add(userService.create(userEntity));
        }

        List<Role> roles = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Role role = new Role();
            role.setId(UUID.randomUUID());
            role.setName("Role" + i);
            roles.add(role);
        }

        HardConstraints constraints = new HardConstraints();
        Map<Role, Integer> requiredRoles = new HashMap<>();
        requiredRoles.put(roles.get(0), 2);
        constraints.setDaytimeRequiredRoles(requiredRoles);
        requiredRoles.put(roles.get(1), 3);
        constraints.setNighttimeRequiredRoles(requiredRoles);
        constraints.setAllowedFlextimePerMonth(20);
        constraints.setAllowedFlextimeTotal(20);
        constraints.setDaytimeRequiredPeople(3);
        constraints.setNighttimeRequiredPeople(2);

        team.setUsers(users);
        team.setShiftTypes(shiftTypes);
        HardConstraints hardConstraints = new HardConstraints();
        hardConstraints.setDaytimeRequiredRoles(new HashMap<>());
        hardConstraints.setNighttimeRequiredRoles(new HashMap<>());
        team.setHardConstraints(new HardConstraints());

        // TODO: fetch from db
        /*
        Optional<Team> team = teamRepository.findById(teamId);

        if (team.isEmpty()) {
            throw new RuntimeException("Could not find team");
        }

        List<User> users = team.get().getUsers();
        HardConstraints constraints = team.get().getHardConstraints();
        List<ShiftType> shiftTypes = team.get().getShiftTypes();
         */

        // map to algorithm input
        AlgorithmMapper algorithmMapper = new AlgorithmMapper();

        AlgorithmInput input =
                algorithmMapper.mapToAlgorithmInput(month, year, users, shiftTypes, roles, constraints, team);

        Optional<AlgorithmOutput> output = SchedulingSolver.solve(input);

        // map from algorithm output
        if (output.isEmpty()) {
            throw new RuntimeException("Could not generate schedule");
        }

        MonthlyPlan monthlyPlan = new MonthlyPlan();

        List<Shift> shifts =
                algorithmMapper.mapFromAlgorithmOutput(output.get(), shiftTypes, users, monthlyPlan, month, year);

        // TODO: create shifts and monthly plan

        monthlyPlan.setMonth(month);
        monthlyPlan.setYear(year);
        monthlyPlan.setTeam(team);
        monthlyPlan.setPublished(false);
        monthlyPlan.setShifts(shifts);

        // monthlyPlanRepository.save(monthlyPlan);
        return monthlyPlan;
    }

}
