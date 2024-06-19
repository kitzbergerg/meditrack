package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.Holiday;
import ase.meditrack.model.entity.MonthlyPlan;
import ase.meditrack.model.entity.Role;
import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.stream.Collectors;

public class AlgorithmMapper {

    private final Map<UUID, Integer> shiftTypeUuidToIndex = new HashMap<>();
    private final Map<UUID, Integer> roleUuidToIndex = new HashMap<>();
    private final Map<Integer, UUID> indexToShiftTypeUuid = new HashMap<>();
    private final Map<Integer, UUID> indexToEmployeeUuid = new HashMap<>();

    /**
     * Converts the input to a format that can be used by the solver.
     *
     * @param month
     * @param year
     * @param employees
     * @param holidaysPerUser
     * @param shiftTypes
     * @param roles
     * @param team
     * @return input for the algorithm
     */
    public AlgorithmInput mapToAlgorithmInput(
            int month,
            int year,
            List<User> employees,
            Map<UUID, List<Holiday>> holidaysPerUser,
            List<ShiftType> shiftTypes,
            List<Role> roles,
            Team team
    ) {
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();


        // Map role entities to records
        List<RoleInfo> roleInfos = new ArrayList<>();
        for (int i = 0; i < roles.size(); i++) {
            // TODO #86: add required people
            Role role = roles.get(i);
            roleInfos.add(new RoleInfo(role.getName(),
                    role.getDaytimeRequiredPeople(),
                    role.getNighttimeRequiredPeople()));
            roleUuidToIndex.put(role.getId(), i);
        }

        int numberOfDays = 0;
        // Create day records for every day of month
        while (!date.isAfter(endDate)) {
            numberOfDays++;
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

            List<Integer> worksShiftTypes = new ArrayList<>();
            if (employee.getCanWorkShiftTypes().isEmpty()) {
                worksShiftTypes.addAll(shiftTypeUuidToIndex.values());
            } else {
                for (ShiftType type : employee.getCanWorkShiftTypes()) {
                    int index = shiftTypeUuidToIndex.get(type.getId());
                    worksShiftTypes.add(index);
                }
            }

            List<Holiday> holidays = holidaysPerUser.get(id);
            Set<Integer> holidayDays = new TreeSet<>();
            for (Holiday holiday : holidays) {
                holiday.getStartDate()
                        .datesUntil(holiday.getEndDate().plusDays(1))
                        .filter(day -> yearMonth.getMonth() == day.getMonth())
                        .forEach(day -> holidayDays.add(day.getDayOfMonth()));
            }

            // we do not consider public holidays or weekends
            // this is close enough for the algorithm to get good results
            float averageWorkingHoursPerDay = employee.getWorkingHoursPercentage() / 100 * team.getWorkingHours() / 7;
            int numberOfWorkingDays = numberOfDays - holidayDays.size();
            int averageWorkingHoursPerMonth = (int) (averageWorkingHoursPerDay * numberOfWorkingDays);

            // TODO #86: get flexitime from role instead of hard constraint
            int maxAllowedChangePlus = Math.min(employee.getRole().getAllowedFlextimePerMonth(),
                    employee.getRole().getAllowedFlextimeTotal() - employee.getCurrentOverTime());
            int maxAllowedChangeMinus = Math.min(employee.getRole().getAllowedFlextimePerMonth(),
                    employee.getRole().getAllowedFlextimeTotal() + employee.getCurrentOverTime());

            employeeInfos.add(new EmployeeInfo(
                    worksShiftTypes,
                    employee.getPreferredShiftTypes().stream().map(type -> shiftTypeUuidToIndex.get(type.getId()))
                            .toList(),
                    averageWorkingHoursPerMonth - maxAllowedChangeMinus,
                    averageWorkingHoursPerMonth + maxAllowedChangePlus,
                    averageWorkingHoursPerMonth - employee.getCurrentOverTime() / 2,
                    holidayDays,
                    employee.getPreferences().getOffDays().stream().map(LocalDate::getDayOfMonth)
                            .collect(Collectors.toSet()),
                    employee.getRole() == null || employee.getRole().getId() == null
                            ? Optional.empty()
                            : Optional.of(roleUuidToIndex.get(employee.getRole().getId()))
            ));
        }

        // TODO #86: add required people
        return new AlgorithmInput(numberOfDays, employeeInfos, shiftTypeInfos, roleInfos,
                team.getHardConstraints().getDaytimeRequiredPeople(),
                team.getHardConstraints().getNighttimeRequiredPeople());
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
    public List<Shift> mapFromAlgorithmOutput(
            AlgorithmOutput output,
            List<ShiftType> shiftTypes,
            List<User> users,
            MonthlyPlan monthlyPlan,
            Integer month,
            Integer year
    ) {
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
