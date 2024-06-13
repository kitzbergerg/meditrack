package ase.meditrack.algorithm;


import ase.meditrack.service.algorithm.AlgorithmInput;
import ase.meditrack.service.algorithm.EmployeeInfo;
import ase.meditrack.service.algorithm.RoleInfo;
import ase.meditrack.service.algorithm.SchedulingSolver;
import ase.meditrack.service.algorithm.ShiftTypeInfo;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverTest {


    @Test
    void simpleTest() {
        List<EmployeeInfo> employeeInfos =
                List.of(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8, 28 * 8 + 20, Set.of()));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename", 0, 0, 0, 0));
        AlgorithmInput input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

    @Test
    void testCanWorkShift() {
        // empty can work -> no solution
        List<EmployeeInfo> employeeInfos =
                List.of(new EmployeeInfo(List.of(), 28 * 8 / 2, 28 * 8, 28 * 8 + 20, Set.of()));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename", 0, 0, 0, 0));
        AlgorithmInput input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isEmpty());

        // set can work -> solution
        employeeInfos = List.of(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8, 28 * 8 + 20, Set.of()));
        input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

    @Test
    void testMaxWorkingHours() {
        // Employee works 28 days with 8 hours each. There is a 20 hour overtime set. So 28 * 8 - 21 -> no solution
        List<EmployeeInfo> employeeInfos =
                List.of(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8 - 21, 28 * 8, Set.of()));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename", 0, 0, 0, 0));
        AlgorithmInput input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isEmpty());

        // 28 * 8 - 20 -> solution
        employeeInfos =
                List.of(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8 + 20, 28 * 8, Set.of()));
        input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

    @Test
    void testHoliday() {
        // holiday with only 1 user -> no solution
        List<EmployeeInfo> employeeInfos = new ArrayList<>();
        employeeInfos.add(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8, 28 * 8 + 20, Set.of(0)));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename", 0, 0, 0, 0));
        AlgorithmInput input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isEmpty());

        // holiday with only 2 user -> solution
        employeeInfos.add(new EmployeeInfo(List.of(0), 28 * 8 / 2, 28 * 8, 28 * 8 + 20, Set.of()));
        input = new AlgorithmInput(28, employeeInfos, shiftTypeInfos, roles, 0, 0);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

}
