package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.HardConstraints;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlgorithmMapper {


    private final Map<UUID, Integer> shiftTypeUuidToIndex = new HashMap<>();
    private final Map<Integer, UUID> indexToShiftTypeUuid = new HashMap<>();
    private final Map<UUID, Integer> employeeUuidToIndex = new HashMap<>();
    private final Map<Integer, UUID> indexToEmployeeUuid = new HashMap<>();
    private final Map<UUID, Integer> roleUuidToIndex = new HashMap<>();

    public AlgorithmInput mapToAlgorithmInput(int month, int year, List<User> employees, List<ShiftType> shiftTypes,
                                              List<Role> roles, HardConstraints constraints, Team team) {

        List<DayInfo> dayInfos = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();


        // Map role entities to records
        List<RoleInfo> roleInfos = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            Role role = roles.get(i);
            UUID id = role.getId();
            roleUuidToIndex.put(id, i);
            roleInfos.add(new RoleInfo(role.getName()));
        }

        // Map required roles
        Map<Role, Integer> dayTimeRoles = constraints.getDaytimeRequiredRoles();
        Map<Role, Integer> nightTimeRoles = constraints.getNighttimeRequiredRoles();
        // Key = index of role, key = required amount of that role
        Map<Integer, Integer> dayTimeRolesMap = new HashMap<>();
        Map<Integer, Integer> nightTimeRolesMap = new HashMap<>();
        for (int i = 0; i < dayTimeRoles.size(); i++) {
            Role role = (Role) dayTimeRoles.keySet().toArray()[i];
            int index = roleUuidToIndex.get(role.getId());
            dayTimeRolesMap.put(index, dayTimeRoles.get(role));
        }
        for (int i = 0; i < nightTimeRoles.size(); i++) {
            Role role = (Role) dayTimeRoles.keySet().toArray()[i];
            int index = roleUuidToIndex.get(role.getId());
            nightTimeRolesMap.put(index, nightTimeRoles.get(role));
        }

        HardConstraintInfo constraintInfo =
                new HardConstraintInfo(dayTimeRolesMap, nightTimeRolesMap, constraints.getDaytimeRequiredPeople(),
                        constraints.getNighttimeRequiredPeople(), constraints.getAllowedFlextimeTotal(),
                        constraints.getAllowedFlextimePerMonth());

        // Create day records for every day of month
        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            boolean isHoliday = false;  // Placeholder
            dayInfos.add(new DayInfo(dayName, isHoliday));
            date = date.plusDays(1);
        }

        // Map shift type entities to records
        List<ShiftTypeInfo> shiftTypeInfos = new ArrayList<>();
        for (int i = 0; i < shiftTypes.size(); i++) {
            ShiftType type = shiftTypes.get(i);
            UUID id = type.getId();
            shiftTypeUuidToIndex.put(id, i);
            indexToShiftTypeUuid.put(i, id);

            shiftTypeInfos.add(new ShiftTypeInfo(type.getStartTime(), type.getEndTime(),
                    type.getEndTime().getHour() - type.getStartTime().getHour()));
        }

        // Map employee entities to records
        List<EmployeeInfo> employeeInfos = new ArrayList<>();
        for (int i = 0; i < employees.size(); i++) {
            User employee = employees.get(i);
            UUID id = employee.getId();
            employeeUuidToIndex.put(id, i);
            indexToEmployeeUuid.put(i, id);

            int workingHours = (int) (employee.getWorkingHoursPercentage() * team.getWorkingHours())
                    - employee.getCurrentOverTime();
            List<Integer> worksShifts = new ArrayList<>();
            for (ShiftType type : employee.getCanWorkShiftTypes()) {
                int index = shiftTypeUuidToIndex.get(type.getId());
                worksShifts.add(index);
            }
            employeeInfos.add(new EmployeeInfo(worksShifts, workingHours));
        }
        AlgorithmInput input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roleInfos, constraintInfo);
        System.out.println(input);
        return input;
    }

    public List<Shift> mapFromAlgorithmOutput(AlgorithmOutput output, List<ShiftType> shiftTypes, List<User> users,
                                              MonthlyPlan monthlyPlan, Integer month, Integer year) {
        // Create maps, key = UUID, value = entity
        Map<UUID, ShiftType> shiftTypeMap =
                shiftTypes.stream().collect(Collectors.toMap(ShiftType::getId, shiftType -> shiftType));
        Map<UUID, User> userMap = users.stream().collect(Collectors.toMap(User::getId, user -> user));

        List<Shift> shifts = new ArrayList<>();
        // Iterate over EmployeeShiftAssignments: Integers representing employee index that have a list of shifts
        for (Map.Entry<Integer, List<AlgorithmOutput.ShiftTypeDayPair>> entry : output.assignmentOfEmployeesToShifts()
                .entrySet()) {
            UUID userUuid = indexToEmployeeUuid.get(entry.getKey()); // map back index to uuid
            User user = userMap.get(userUuid);

            if (user != null) {
                for (AlgorithmOutput.ShiftTypeDayPair pair : entry.getValue()) { // Iterate over shifts the employee has
                    UUID shiftTypeUuid = indexToShiftTypeUuid.get(pair.shiftType());
                    ShiftType shiftType = shiftTypeMap.get(shiftTypeUuid);

                    if (shiftType != null) {
                        Shift shift = new Shift();
                        shift.setShiftType(shiftType);
                        shift.setUsers(List.of(user));
                        shift.setMonthlyPlan(monthlyPlan);
                        shift.setDate(LocalDate.of(year, month, pair.day() + 1));
                        shifts.add(shift);
                    }
                }
            }
        }
        return shifts;
    }

}
