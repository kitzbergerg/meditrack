package ase.meditrack.algorithm;


import ase.meditrack.service.algorithm.AlgorithmInput;
import ase.meditrack.service.algorithm.DayInfo;
import ase.meditrack.service.algorithm.EmployeeInfo;
import ase.meditrack.service.algorithm.HardConstraintInfo;
import ase.meditrack.service.algorithm.RoleInfo;
import ase.meditrack.service.algorithm.SchedulingSolver;
import ase.meditrack.service.algorithm.ShiftTypeInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

class SolverTest {


    @Test
    void firstTest() {

        List<EmployeeInfo> employeeInfos = List.of(new EmployeeInfo(List.of(1, 2, 3), 90));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<DayInfo> dayInfos = new ArrayList<>();
        for (int i = 0; i < 28; i++) {
            DayInfo day = new DayInfo("Dayname", false);
            dayInfos.add(day);
        }
        RoleInfo role = new RoleInfo("Rolename");
        List<RoleInfo> roles = new ArrayList<>();
        roles.add(role);
        HardConstraintInfo hardConstraints = null;
        AlgorithmInput input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        var solution = SchedulingSolver.solve(input);

    }

}
