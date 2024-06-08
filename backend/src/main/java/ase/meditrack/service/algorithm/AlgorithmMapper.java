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
    private final Map<Integer, UUID> indexToEmployeeUuid = new HashMap<>();

    /**
     * Converts the input to a format that can be used by the solver.
     *
     * @param month
     * @param year
     * @param employees
     * @param shiftTypes
     * @param roles
     * @param constraints
     * @param team
     * @return input for the algorithm
     */
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
            roleInfos.add(new RoleInfo(role.getName()));
        }

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

            int duration;
            if (type.getEndTime().isBefore(type.getStartTime())) {
                duration = 24 - type.getStartTime().getHour() + type.getEndTime().getHour();
            } else {
                duration = type.getEndTime().getHour() - type.getStartTime().getHour();
            }
            shiftTypeInfos.add(new ShiftTypeInfo(type.getStartTime(), type.getEndTime(), duration));
        }

        // Map employee entities to records
        List<EmployeeInfo> employeeInfos = new ArrayList<>();
        for (int i = 0; i < employees.size(); i++) {
            User employee = employees.get(i);
            UUID id = employee.getId();
            indexToEmployeeUuid.put(i, id);

            // TODO #86: make sure holidays and off days are considered in this calculation
            int optimalWorkingHoursPerMonth =
                    (int) (employee.getWorkingHoursPercentage() / 100 * team.getWorkingHours());
            optimalWorkingHoursPerMonth = optimalWorkingHoursPerMonth * dayInfos.size() / 5;

            List<Integer> worksShifts = new ArrayList<>();
            if (employee.getCanWorkShiftTypes().isEmpty()) {
                worksShifts.addAll(shiftTypeUuidToIndex.values());
            } else {
                for (ShiftType type : employee.getCanWorkShiftTypes()) {
                    int index = shiftTypeUuidToIndex.get(type.getId());
                    worksShifts.add(index);
                }
            }
            employeeInfos.add(new EmployeeInfo(worksShifts, optimalWorkingHoursPerMonth));
        }

        // TODO #86: add required people
        return new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roleInfos, 0, 0);
    }

    /**
     * Converts the solvers output back to entities that will then be created.
     *
     * @param output
     * @param shiftTypes
     * @param users
     * @param monthlyPlan
     * @param month
     * @param year
     * @return a list of shifts that satisfy the constraints
     */
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

            if (user == null) continue;

            for (AlgorithmOutput.ShiftTypeDayPair pair : entry.getValue()) { // Iterate over shifts the employee has
                UUID shiftTypeUuid = indexToShiftTypeUuid.get(pair.shiftType());
                ShiftType shiftType = shiftTypeMap.get(shiftTypeUuid);
                if (shiftType == null) continue;

                Shift shift = new Shift();
                shift.setShiftType(shiftType);
                shift.setUsers(List.of(user));
                shift.setMonthlyPlan(monthlyPlan);
                shift.setDate(LocalDate.of(year, month, pair.day() + 1));
                shifts.add(shift);
            }
        }
        return shifts;
    }

}
