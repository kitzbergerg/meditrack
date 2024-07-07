## Boolean Variables:

- **Input**:
    - Month, Year
    - DepartmentManagerId/Team

1. **Employees (`n`)**: Integers representing individual employees of the team.
2. **Days (`d`)**: Integers representing each day in the scheduling period.
3. **Shift Types (`s`)**: Integers representing different types of shifts (e.g., morning, evening, night).

Each entry in this array (`shifts[n][d][s]`) is a Boolean variable (`Literal`) that indicates whether employee `n` is
assigned to shift type `s` on day `d`. The value is `true` if the employee is scheduled for that shift on that day,
and `false` otherwise.

## Constraints & Required Inputs:

Most constraints need **allEmployees**, **allDays**, **allShifts** to iterate over **shifts\[n\]\[d\]\[s\]**.

### Terminology

- Role Constraint: Hard Contraint for a specific role
- Constraint: General Hard Constraint, for all roles
- User Constraint: Constraint for a specific user

### Hard Constraints

- **One Shift Per Day**
    - **Input**: allEmployees, allDays, allShifts
    - **Explanation**: An employee can only work 1 shift per day
    - [x] done
- **Shift Compatibility (User Constraint)**
    - **Input**:
        - Employee.worksShiftTypes
    - **Explanation**: An employee can only work shifts they agreed to
    - [x] done
- **Maximum and Minimum Monthly Hours (User Constraint)**
    - **Input**:
        - Employee.workingHoursPercentage
        - Employee.currentOvertime
        - Team.workingHours
    - **Explanation**: Set the min/max allowed hours per month
    - Example for HashMap: employeeMonthlyMaxHours.put(0, 190); Employee 0 can work for max. 190 hours.
    - [x] done
- **Maximum Hours per Week (Role Constraint)** (Legal Constraint - max. 48 per week / 70 if high workload)
    - **Input**:
        - ShiftType.duration / ShiftType.endType - ShiftType.startTime
        - Role.allowedFlexitimeTotal (maybe minimize instead)
        - Role.allowedFlexitimeMonthly (maybe minimize instead)
    - [x] done
- **Maximum Consecutive Shifts/Hours (Not a constraint)**
    - **Input**:
        - HardConstraints.maxWeeklyHours/maxConsecutiveShifts
        - ShiftType.duration / ShiftType.endType - ShiftType.startTime
    - [x] done
- **Staffing Level Per Day/Nighttime (Constraint)**
    - **Input**:
        - HardConstraints.daytimeRequiredPeople
        - HardConstraints.nighttimeRequiredPeople
        - ShiftType.startTime
        - ShiftType.endTime
    - [x] done
- **Staffing Level Per Day/Nighttime Per Role (Role Constraint)**
    - **Input**:
        - Role.daytimeRequiredPeople
        - Role.nighttimeRequiredPeople
        - ShiftType.startTime
        - ShiftType.endTime
    - [x] done
- **Holidays (User Constraint)**
    - **Input**:
        - Employee.Holidays.startTime/endTime
    - [x] done
- **NightShift/DayShift change (Constraint)**
    - **Input**:
        - ShiftType.startTime
        - ShiftType.endTime
    - **Explanation**: If an employee works a nightshift, the next day has to be either not a working day, or another nightshift
    - [x] done

### Optimizations

- **Keep overtime close to 0**
    - [x] done
- **OffDays**
    - **Input**:
        - Employee.Preferences.offDays
    - [x] done
- **Make sure employees tend to work the same/similar shift types for a given month**
    - [x] done
- **Make sure employees work their preferred shifts**
    - [x] done
- **Evenly Distribute Hours per week**
    - [x] done