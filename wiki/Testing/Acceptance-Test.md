# Acceptance Test

## Scenarios

### Department Manager (DM)

#### Scenario 1: View and Filter Users

- **Precondition:** DM is logged in.
- **Steps:**
  1. Navigate to the user management section.
  2. View the list of users.
  3. Apply filters to the user list.
- **Acceptance Criteria:**
  - User list is displayed.
  - Filters are applied correctly.

#### Scenario 2: Create Roles and Shift Types

- **Precondition:** DM is logged in.
- **Steps:**
  1. Navigate to the roles and shift types section.
  2. Create/Edit a (new) role.
  3. Create/Edit a (new) shift type.
- **Acceptance Criteria:**
  - Role is created successfully.
  - Shift type is created successfully.

#### Scenario 3: View Rules

- **Precondition:** DM is logged in.
- **Steps:**
  1. Navigate to the rules section.
  2. View existing rules.
- **Acceptance Criteria:**
  - Rules are displayed correctly.

#### Scenario 4: Create and Edit Schedule

- **Precondition:** DM is logged in.
- **Steps:**
  1. Navigate to the schedule creation section.
  2. Create a new schedule.
  3. Edit the existing schedule.
- **Acceptance Criteria:**
  - Schedule is created successfully.
  - Schedule is edited and saved correctly.

#### Scenario 5: Mark Someone as Sick and Find a Replacement

- **Precondition:** DM is logged in.
- **Steps:**
  1. Navigate to the schedule section.
  2. Mark an employee as sick.
  3. Find and assign a replacement.
- **Acceptance Criteria:**
  - Employee is marked as sick.
  - Replacement is found and assigned correctly.


### Employee

#### Scenario 1: Log in and View Profile

- **Precondition:** Employee has a valid account.
- **Steps:**
  1. Open the application.
  2. Enter username and password.
  3. Click 'Log in'.
  4. Navigate to the profile section.
- **Acceptance Criteria:**
  - Employee successfully logs in.
  - Profile information is displayed correctly.

#### Scenario 2: View and Change Schedule

- **Precondition:** Employee is logged in.
- **Steps:**
  1. Navigate to the schedule section.
  2. View current schedule.
  3. Change the view (e.g., daily, weekly, monthly).
- **Acceptance Criteria:**
  - Schedule is displayed correctly.
  - Schedule view changes as selected.

#### Scenario 3: Request Holidays

- **Precondition:** Employee is logged in.
- **Steps:**
  1. Navigate to the holidays section.
  2. Request a new holiday.
  3. Submit the request.
- **Acceptance Criteria:**
  - Holiday request form is available.
  - Request is submitted and recorded correctly.

#### Scenario 4: Offer a Shift and Swap 

- **Precondition:** Employee is logged in.
- **Steps:**
  1. Navigate to the shift swap section.
  2. Select a shift to offer for swapping.
  3. Submit the offer.
  4. Select and Swap one own and one shift from a colleague 
  5. Confirm the swap.
- **Acceptance Criteria:**
  - Shift is available for swapping.
  - Offer is submitted and visible for other employees.
  - Shift swap is processed successfully.
  - New shift is updated in the schedule.


## Feedback

### Overall 

- System is intuitive after initial use.
- Created schedule is realistic.
- Design looks good.
- Menu icon is too far right, resulting in confusion to where it belongs

### Bugs/Problems

- Creating Role results in 403 error but editing was successful.
- Error is not always displayed correctly ([Object object] error messages)

### Overview of Roles and Shift Types

- Add cursor pointer when hovering over roles and shift types, should be uniform.
- Editing was not intuitive, since the edit button enables the edit mode.

### Rules

- Ensure working hours reflect 100% employment equals 40 hours per week.

### Holidays

- Filter requested shifts.
- Display remaining holiday days/weeks for the current year.

### Calendar

- Display overtime for this month and total overtime, including last month's overtime.
- Show today’s range with a time view.
- Change buttons for sick leave.
- Use a common color for sick shifts.
- Provide user info details in the legend.

### Employee Dashboard

- Display overtime, working hours for this month, today's/next shift, and coworkers.

### Off Days

- First come, first serve policy might be useful.

### Shift Swap

- Implement sorting and compatibility checks (validation before the request is sent).
- Show a pop-up after selecting two shifts.

### Data Generation

- Ensure shifts overlap by 30 minutes.
