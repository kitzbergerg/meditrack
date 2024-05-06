
#### Boolean Variables:

- **Input**:
	- Month, Year
	- DepartmentManagerId/Team 

1.  **Nurses (`n`)**: Indexed from `0` to `numNurses - 1`, representing individual nurses available for shifts.
2.  **Days (`d`)**: Indexed from `0` to `numDays - 1`, representing each day in the scheduling period.
3.  **Shift Types (`s`)**: Indexed from `0` to `numShifts - 1`, representing different types of shifts (e.g., morning, evening, night).

- **int [] allNurses**: Array filled with ints 0..numNurses-1
- **int [] allDays**: Array filled with ints 0..numDays-1
- **int [] allShifts**: Array filled with ints 0..numShifts-1

Each entry in this array (`shifts[n][d][s]`) is a Boolean variable (`Literal`) that indicates whether nurse `n` is assigned to shift type `s` on day `d`. The value is `true` if the nurse is scheduled for that shift on that day, and `false` otherwise.

#### Constraints & Required Inputs:

Almost every constraint needs **allNurses**, **allDays**, **allShifts** to iterate over **shifts[n][d][s]**. 

- **One Shift Per Day**
    
    -   **Input**: allNurses, allDays, allShifts

-  **Shift Compatibility**
    
    -   **Input**:
	    -  Nurse.worksShiftTypes
	- Example for HashMap:  nurseShiftCompatibility.put(0, Arrays.asList(0, 1, 2)); Nurse 0 can work Shifts with index 0,1,2.

- **Maximum and Minimum Monthly Hours**
    -   **Input**: 
	    - Nurse.workingHoursPercentage
	    - Nurse.currentOvertime
	    - Team.workingHours;
	- Example for HashMap: nurseMonthlyMaxHours.put(0, 190); Nurse 0 can work for max. 190 hours. 

-  **Maximum Consecutive Shifts/Hours**
    
    -   **Input**: 
	    - HardConstraints.maxWeeklyHours/maxConsecutiveShifts
	    - ShiftType.duration / ShiftType.endType - ShiftType.startTime
	
- **Maximum Hours per Week** (Legal Constraint - max. 48 per week / 70 if high workload)
	-   **Input**:
		- ShiftType.duration / ShiftType.endType - ShiftType.startTime
		- HardConstraints.allowedFlexitimeTotal (maybe minimize instead)
		- HardConstraints.allowedFlexitimeMonthly (maybe minimize instead)

-  **Minimum Total Nurses/Staffing Level Per Day/Night**
    -   **Input**: 
	    - HardConstraints.daytimeRequiredPeople
	    - HardConstraints.nighttimeRequiredPeople
	    - ShiftType.type(day/night/etc.)

-  **Minimum Nurses Per Shift** 
    -   **Input**: 
	    - HardConstraints.daytimeRequiredRoles
	    - HardConstraints.nighttimeRequiredRoles
- ** TODO: Min Nights shifts - max**
	    -  
-  **OffDays/Holidays** 
    -   **Input**: 
	    - Nurse.Holidays.startTime/endTime
	    - Nurse.Preferences.offDays

#### Optimizations:

-   **Maximize Consecutive Shifts**
    -   **Encoding**: Variables ( `consecShifts[n][d][s]`) created for consecutive Shifts, only true if shifts[n][d][s] and shifts[n][d-1][s].

- **Evenly Distribute Hours per week**
- **Evenly Distribute Worked Shifts**
- **Evenly Distribute Weekend/public Holidays**
- **Minimize Overtime/Reduced hours **