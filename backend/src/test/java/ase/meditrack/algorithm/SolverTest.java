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

import static org.junit.jupiter.api.Assertions.assertTrue;

class SolverTest {


    @Test
    void simpleTest() {
        List<EmployeeInfo> employeeInfos = List.of(new EmployeeInfo(List.of(0), 100000));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<DayInfo> dayInfos = new ArrayList<>();
        for (int i = 0; i < 28; i++) dayInfos.add(new DayInfo("Dayname_" + i, false));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename"));
        HardConstraintInfo hardConstraints = null;
        AlgorithmInput input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

    @Test
    void testCanWorkShift() {
        // empty can work -> no solution
        List<EmployeeInfo> employeeInfos = List.of(new EmployeeInfo(List.of(), 100000));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<DayInfo> dayInfos = new ArrayList<>();
        for (int i = 0; i < 28; i++) dayInfos.add(new DayInfo("Dayname_" + i, false));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename"));
        HardConstraintInfo hardConstraints = null;
        AlgorithmInput input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        assertTrue(SchedulingSolver.solve(input).isEmpty());

        // set can work -> solution
        employeeInfos = List.of(new EmployeeInfo(List.of(0), 100000));
        input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

    @Test
    void testMaxWorkingHours() {
        // Employee works 28 days with 8 hours each. There is a 20 hour overtime set. So 28 * 8 - 21 -> no solution
        List<EmployeeInfo> employeeInfos = List.of(new EmployeeInfo(List.of(0), 28 * 8 - 21));
        List<ShiftTypeInfo> shiftTypeInfos = List.of(new ShiftTypeInfo(LocalTime.of(8, 0), LocalTime.of(18, 0), 8));
        List<DayInfo> dayInfos = new ArrayList<>();
        for (int i = 0; i < 28; i++) dayInfos.add(new DayInfo("Dayname_" + i, false));
        List<RoleInfo> roles = List.of(new RoleInfo("Rolename"));
        HardConstraintInfo hardConstraints = null;
        AlgorithmInput input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        assertTrue(SchedulingSolver.solve(input).isEmpty());

        // 28 * 8 - 20 -> solution
        employeeInfos = List.of(new EmployeeInfo(List.of(0), 28 * 8 - 20));
        input = new AlgorithmInput(employeeInfos, shiftTypeInfos, dayInfos, roles, hardConstraints);
        assertTrue(SchedulingSolver.solve(input).isPresent());
    }

}
