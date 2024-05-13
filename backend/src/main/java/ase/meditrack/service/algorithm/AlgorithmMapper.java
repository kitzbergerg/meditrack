package ase.meditrack.service.algorithm;

import ase.meditrack.model.entity.Shift;
import ase.meditrack.model.entity.ShiftType;
import ase.meditrack.model.entity.Team;
import ase.meditrack.model.entity.User;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.IntStream;

@Component
public class AlgorithmMapper {

    public AlgorithmInput mapToAlgorithmInput(int month, int year, List<User> employees, List<ShiftType> shiftTypes, Team team) {

        // Potentially complex mapping logic goes here

        List<DayInfo> dayInfos = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate date = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        while (!date.isAfter(endDate)) {
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            String dayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH);
            boolean isHoliday = false;  // Placeholder
            dayInfos.add(new DayInfo(date.getDayOfMonth(), dayName, isHoliday));
            date = date.plusDays(1);
        }

        List<ShiftTypeInfo> shiftTypeInfos = new ArrayList<>();
        for (ShiftType type: shiftTypes) {
            shiftTypeInfos.add(new ShiftTypeInfo(type.getId(), type.getStartTime(), type.getEndTime(), type.getStartTime().getHour()-type.getEndTime().getHour()));
        }

        List<EmployeeInfo> employeeInfos = new ArrayList<>();
        for (User employee: employees) {
            int workingHours = (int) (employee.getWorkingHoursPercentage() * team.getWorkingHours()) - employee.getCurrentOverTime();
            List<Integer> worksShifts = new ArrayList<>();
            for (ShiftType type : employee.getCanWorkShiftTypes()) {
                int index = IntStream.range(0, shiftTypes.size())
                        .filter(i -> shiftTypes.get(i).getId() == type.getId())
                        .findFirst()
                        .orElseThrow();
                worksShifts.add(index);
            }
            employeeInfos.add(new EmployeeInfo(employee.getId(), worksShifts ,workingHours));
        }

        HardConstraintInfo constraintInfos = new HardConstraintInfo();

        return new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, constraintInfos);
    }

    public List<Shift> mapFromAlgorithmOutput(AlgorithmOutput output) {
        // Conversion logic from algorithm output to domain entities
        // This could involve complex logic based on the algorithm's results
        return new ArrayList<>(); // Example stub
    }

}
