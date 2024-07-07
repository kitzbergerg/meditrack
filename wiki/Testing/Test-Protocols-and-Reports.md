This wiki page contains a protocol and report for every manual system test.

# <a name="enums">Enumerations</a>
Use the following styling for the **Test Result** column:
- `Error-free` $`\textcolor{green}{\textsf{Error-free}}`$
- `Faulty` $`\textcolor{red}{\textsf{Faulty}}`$
- `Blocked` $`\textcolor{yellow}{\textsf{Blocked}}`$

Column **Classification** contains the class number based on the following breakdown:
- `Class 1` Incorrect Specification
- `Class 2` System Crash
- `Class 3` Essential functionality is faulty
- `Class 4` Functional deviation or limitation
- `Class 5` Minor deviation
- `Class 6` Cosmetic issues

Column **Prioritization** contains the level number based on the following breakdown:
- `Level 1` Immediate Fix
- `Level 2` Fix in the next version
- `Level 3` Correction will be made at the next opportunity
- `Level 4` Correction planning is still open

<!-- TODO: copy the template and replace "X" with the number of the system test and fill in the document -->
# Test Protocol & Test Report for [System Test #X](/Testing/Manual-System-Tests#manual-system-test-X)

## Test Protocol

**Tester**:

**Date**:

**Time**:

**Duration**:

**Number of Revision/Version**:

### Test Cases

| Nr. | Expected Outcome | Actual Outcome | [Test Result](#enums) | 
|-----|------------------|----------------|-------------------------|
|     |                  |                |                         |

## Test Report

**Total Number of Test Cases**:

**Performed Test Cases**:

**Passed Test Cases**:

**Failed Test Cases**:

**Noted Errors**:
- .
- .
- .

## Error Analysis

| Error / Faulty Test Case | New / Known | [Classification](#enums) | [Prioritization](#enums) | 
|---|---|------------------|------------------|
| | |                  |                  |

## Result

**How would you assess the state of the software?**

*Answer*

**Have the quality goals been achieved?**

*Answer*

**What consequences are drawn from the current state, including: How can future errors be avoided, how can the development process be improved?**

*Answer*


# Test Protocol & Test Report for [System Test #1](/Testing/Manual-System-Tests#Manual-System-Test-1)

## Test Protocol

**Tester**: Gülsüm Gülmez

**Date**: 08/05/2024

**Time**: 08:40

**Duration**: 50 minutes

**Number of Revision/Version**: f0d1292f

### Test Cases

| Nr. | Expected Outcome | Actual Outcome | [Test Result](#enums) | 
|-----|------------------|----------------|-------------------------|
| 1   | Docker daemon is running | Docker is actually running | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 2   | Docker can build docker images | Successfully built images | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 3   | Docker can start all containers | Containers start successfully without errors | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 4   | Checking in Docker Desktop if backend is available | Status of backend container is `Running` and backend application loads without errors on browser | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 5   | Sending requests to backend url '/test' responses with appropriate status code | Status code of response is `200 OK` | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 6   | Frontend is available and can be accessed through the web browser | Frontend application loads without errors | $`\textcolor{red}{\textsf{Faulty}}`$ |
| 7   | Admin can login | Admin can login without problems and is navigated to the homepage | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 8   | Keycloak is available and can be accessed through the web browser | Keycloak admin console loads successfully (login page is shown) | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 9   | Ensure PostgreSQL database is accessible | Successful connection established and it is possible to execute SQL queries | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 10  | Docker can stop all containers | Containers stop successfully without errors | $`\textcolor{green}{\textsf{Error-free}}`$ |

## Test Report

**Total Number of Test Cases**: 8

**Performed Test Cases**: 8

**Passed Test Cases**: 7

**Failed Test Cases**: 1

**Noted Errors**:
- Frontend shows nginx welcome page instead of MediTrack's login page if started with docker. Separately started with `npm start` works fine.

## Error Analysis

| Error / Faulty Test Case | New / Known | [Classification](#enums) | [Prioritization](#enums) | 
|---|---|------------------|------------------|
| 6 | New | `Class 5`          |`Level 3`|

## Result

**How would you assess the state of the software?**

The software is in its early stages with the first version undergoing testing. While most functionalities are error-free, there's an issue with the frontend not loading correctly when started with Docker, although it functions properly when started separately with `npm start`.

**Have the quality goals been achieved?**

The quality goals have been partially achieved. While some functionalities are error-free, there's a notable issue with the frontend loading when started with Docker.

**What consequences are drawn from the current state, including: How can future errors be avoided, how can the development process be improved?**

To address the current issue and prevent future errors:

- Debug the frontend Docker startup to ensure consistency with local development environments.
- Provide clear documentation on Docker setup.

# Test Protocol & Test Report for [System Test #2](/Testing/Manual-System-Tests#manual-system-test-2)

## Test Protocol

**Tester**: Gülsüm Gülmez

**Date**: 27/05/2024

**Time**: 12:20

**Duration**: 55 minutes

**Number of Revision/Version**: 96276cce

### Test Cases

| Nr. | Expected Outcome                                             | Actual Outcome                                               | [Test Result](#enums)                      | 
|-----|--------------------------------------------------------------|--------------------------------------------------------------|--------------------------------------------|
| 1   | Successful login                                             | Successful login                                             | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 2   | Successful login with using new password                     | Successful login with using new password                     | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 3   | Successful creation of role                                  | Successful creation of role                                  | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 4   | Failed creation of role - error message as notification      | Failed creation of role - error message as notification      | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 5   | Successfully saved changes in the db                         | Successfully saved changes in the db                         | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 6   | Successfully removed chosen role from db                     | Successfully removed chosen role from db                     | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 7   | Successful creation of shift type                            | Successful creation of shift type                            | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 8   | Failed creation of shift type -error message as notification | Failed creation of shift type -error message as notification | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 9   | Successfully saved changes in the db                         | Successfully saved changes in the db                         | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 10  | Successfully removed chosen shift type from db               | Successfully removed chosen shift type from db               | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 11  | Successful creation of employee in the db                    | Successful creation of employee in the db                    | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 12  | Successful login                                             | Successful login                                             | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 13  | Successfully changed off days                                | Successfully changed off days                                | $`\textcolor{green}{\textsf{Error-free}}`$ |
| 14  | Should not change off days                                   | Successfully changed off days                                | $`\textcolor{red}{\textsf{Faulty}}`$       |
| 15  | Successfully changed language                                | Successfully changed language                                | $`\textcolor{green}{\textsf{Error-free}}`$ |



## Test Report

**Total Number of Test Cases**: 15

**Performed Test Cases**: 15

**Passed Test Cases**: 14

**Failed Test Cases**: 1

**Noted Errors**:
- cancelling rule editing -> not saved change is still visible after cancelling

## Error Analysis

| Error / Faulty Test Case | New / Known | [Classification](#enums) | [Prioritization](#enums)         | 
|--------------------------|-------------|-------------------------|----------------------------------|
| 14                       | New         | `Class 5`                | `Level 1` |

## Result

**How would you assess the state of the software?**

The software is generally functioning well, with 14 out of 15 test cases passing. The main issue identified is related to test case 14, where cancelled rule edits are not properly reverted.

**Have the quality goals been achieved?**

Most quality goals have been met, with a high pass rate of 93.3%.

**What consequences are drawn from the current state, including: How can future errors be avoided, how can the development process be improved?**

1. Error Resolution: Fix the issue in test case 14.
2. Enhanced Testing: Focus more on scenarios involving data state changes and cancellations.

# Test Protocol & Test Report for [System Test #3](/Testing/Manual-System-Tests#manual-system-test-3)

## Test Protocol

**Tester**: Gülsüm Gülmez

**Date**: 28.06.2024

**Time**: 10:30

**Duration**: 1 hour 30 minutes

**Number of Revision/Version**: c81c1fbf

### Test Cases

| Nr. | Expected Outcome                                                                         | Actual Outcome                                                                                      | [Test Result](#enums)                       | 
|-----|------------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|---------------------------------------------|
| 1   | DM can create plan for July - next month.                                                | Successful creation of plan                                                                         | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 2   | Dm can publish the created plan.                                                         | Successful published plan and user of team can see the plan                                         | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 3   | Employee can create PDF of the plan                                                      | Successfully downloaded plan PDF                                                                    | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 4   | Employee can choose a third preferred off day.                                           | Successfully saved third off day                                                                    | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 5   | Employee can request a holiday if there is a holiday for the same date already requested | Successfully saved holiday request*                                                                 | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 6   | Employee can request a holiday if there is a off day of preference already chosen        | Successfully saved holiday request*                                                                 | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 7   | Employee can edit a holiday request if not already approved/rejected                     | Successfully edited and saved request                                                               | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 8   | DM can approve holiday requested holiday                                                 | Successfully approved holiday request and user can see that it is approved                          | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 9   | DM can add shifts manually to created plan                                               | Successfully saved new shift and user can see it in his schedule                                    | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 10  | DM can edit rules                                                                        | Successfully saved the updated rule                                                                 | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 11  | DM can mark employee's shift as Sick Leave and set another employee as replacement       | Successfully marked shift as sick leave and the replacement can see their new shift in the schedule | $`\textcolor{green}{\textsf{Error-free}}`$  |
| 12  | Employees can create shift swap offers                                                   | Successfully marked shift as offered and other employees can see it now                             | $`\textcolor{green}{\textsf{Error-free}}`$  |

## Test Report

**Total Number of Test Cases**: 12

**Performed Test Cases**: 12

**Passed Test Cases**: 12

**Failed Test Cases**: 0

**Noted Errors**:
- no errors found

## Error Analysis

| Error / Faulty Test Case | New / Known | [Classification](#enums) | [Prioritization](#enums) | 
|--------------------------|-------------|--------------------------|--------------------------|
| -                        | -           | -                        | -                        |

## Result

**How would you assess the state of the software?**

The software is in a stable and reliable state, as all test cases have passed without any errors. This indicates that the current version of the software meets the expected functionality and performance criteria.

**Have the quality goals been achieved?**

Yes, the quality goals have been achieved. The software has successfully met all the outlined test cases without any errors, demonstrating that it functions correctly and is ready for deployment.

**What consequences are drawn from the current state, including: How can future errors be avoided, how can the development process be improved?**

Regular testing, detailed documentation, establishing a user feedback loop, conducting code reviews, implementing a CI/CD pipeline, and providing ongoing training for teams will help avoid future errors and improve the development process.
