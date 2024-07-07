
# Nurse Scheduling Algorithm

## Overview
This algorithm is designed to generate a monthly shift schedule for nurses (currently using Google OR-Tools), ensuring efficient shift distribution while complying with operational and legal constraints.

## Monthly Scheduling Algorithm
### Goals
- Generate a possible shift schedule for nurses at the beginning of each month.

### Non-Goals
- Managing total working time.
- Handling shift swaps post-scheduling.
- Considering employee specializations.

## Inputs
- **Shift-type**: List detailing each shift's name, start, and end time, e.g., `[(L7, 7:30, 19:30), (L8, 7:00, 15:00), (N1, 19:00, 7:30)]`. The most common types in practice are day (12hrs) and night (12hrs) shifts.
- **Employees**: Information on each nurse including compatible shifts, role, preferences (off days, shift types), planned working minus overtime, and holidays.

## Constraints
- **Single Shift Per Day**: Nurses are assigned only one shift type per day.
- **Shift/Off-shift Patterns**: Days off are required after a certain amount of consecutive shifts.
- **Required Roles** Required roles and amount of nurses per shift.
-  **Staffing levels**: Minimum staffing levels per day/night and minimum nurses per role for shifts.
- **Overtime**: Specifies maximum overtime per week/month.

## Austrian Labor Law Constraints
- **Daily Working Hours**: The maximum allowable working hours in a 24-hour period is 13 hours.
- **Weekly Working Hours**: The average working hours from Monday to Sunday must not exceed 48 hours, with a possibility of extending up to 60 hours in certain weeks within a 17-week period.
- **Rest Periods**: A minimum of 30 minutes break is required if the workday exceeds six hours.
- **Daily Rest**: At least 11 hours of continuous rest is mandatory after finishing daily work.

## Smart shift scheduling
Nurses typically work in shift blocks (eg. 2 Day shifts - 2 Night shifts - 2 Off days). It is important that the algorithm schedules in a similar manner.  

Instead of the DM having to create these shift packages, we can optimize objective functions. This means we only consider solutions that maximize our scheduling goals/needs. 
- **Maximize Consecutive Shifts**: Strives to maximize consecutive shifts of the same type for each nurse, aiming to reduce transitions and maintain schedule consistency.
- **To be continued...**: Multiply such objective functions are probably necessary for a sensible schedule.