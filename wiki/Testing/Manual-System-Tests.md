This wiki page contains a table with the test cases of every manual system test.

**Type of test case**:
- NC (Normal case)
- EC (Error case)
- SC (Special case)

<!-- TODO: copy the template and replace the "X" with the number of the current system test -->
# <a name="Manual-System-Test-#X">Manual System Test #X</a>

| Nr. | NC / EC / SC | Description | Prerequisite | Input | Expected Outcome |
|-----|--------------|-------------|--------------|-------|------------------|
|     |              |             |              |       |                  |


# <a name="Manual-System-Test-#1">Manual System Test #1</a>

| Nr. | NC / EC / SC | Description | Prerequisite | Input | Expected Outcome |
|-----|--------------|-------------|--------------|-------|------------------|
| 1   | NC           | Docker daemon has to be running | Docker/Docker Desktop installed | - | Docker is actually running |
| 2   | NC           | Docker can build docker images | Docker is installed | `docker compose --profile keycloak --profile backend --profile frontend build` | Successfully built images |
| 3   | NC           | Docker can start all containers | Docker installed, container image available | `docker compose --profile keycloak --profile backend --profile frontend up -d` | Containers start successfully without errors |
| 4   | NC           | Checking in Docker Desktop if backend is available | Backend container started | - | Status of backend container is `Running` and backend application loads without errors on browser |
| 5   | NC           | Sending requests to backend url '/test' responses with appropriate status code | Backend container started | Send get request from Postman to 'http://localhost:8081/api/test' | Status code of response is `200 OK`. |
| 6   | NC           | Frontend is available and can be accessed through the web browser | Frontend container started | Open web browser and navigate to `http://localhost:4200/` | Frontend application loads without errors |
| 7   | NC           | Admin can login | Frontend container started | Type 'admin' as login credentials | Admin can login without problems and is navigated to the homepage |
| 8   | NC           | Keycloak is available and can be accessed through the web browser | Keycloak started and configured | Open web browser and navigate to `http://localhost:8080/` | Keycloak admin console loads successfully (login page is shown) |
| 9   | NC           | Ensure PostgreSQL database is accessible | PostgreSQL configured and running | Connect to PostgreSQL database e.g. using IntelliJ IDE with specified user and password | Successful connection established and it is possible to execute SQL queries |
| 10  | NC           | Docker can stop all containers | Docker installed, containers running | `docker compose --profile keycloak --profile backend --profile frontend down` | Containers stop successfully without errors |



# <a name="Manual-System-Test-#2">Manual System Test #2</a>

| Nr. | NC / EC / SC | Description                                             | Prerequisite             | Input                                                                                                                               | Expected Outcome                                             |
|-----|--------------|---------------------------------------------------------|--------------------------|-------------------------------------------------------------------------------------------------------------------------------------|--------------------------------------------------------------|
| 1   | NC           | DM can login                                            | DM Account created       | DM username and password                                                                                                            | Successful login                                             |
| 2   | NC           | DM can change password                                  | DM Account logged in     | New password                                                                                                                        | Successful login with using new password                     |
| 3   | NC           | Roles can be created                                    | DM logged in             | Role name, color, abbreviation                                                                                                      | Successful creation of role                                  |
| 4   | NC           | Roles have to be unique in name, color and abbreviation | DM logged in             | (same as TC 3) Role name, color, abbreviation                                                                                       | Failed creation of role - error message as notification      |
| 5   | NC           | Roles can be edited                                     | Role created             | Change name from "Role" to "RoleTest"                                                                                               | Successfully saved changes in the db                         |
| 6   | NC           | Roles can be deleted                                    | Role created             | Click on "Delete" button                                                                                                            | Successfully removed chosen role from db                     |
| 7   | NC           | Shift Types can be created                              | DM logged in             | Shift Type name, color, abbreviation, start and end times of shift and break, type                                                  | Successful creation of shift type                            |
| 8   | NC           | Shift Types cannot be created if validation fails       | DM logged in             | Shift Type name, color, abbreviation, start and end times of shift and break, type - where break start time is after break end time | Failed creation of shift type -error message as notification |
| 9   | NC           | Shift Type can be edited                                | Shift type created       | Change name from "Day Shift" to "Day Time"                                                                                          | Successfully saved changes in the db                         |
| 10  | NC           | Shift Type can be deleted                               | Shift type created       | Click on "Delete" button                                                                                                            | Successfully removed chosen shift type from db               |
| 11  | NC           | DM can create new employee account                      | Employee data prepared   | Employee data + created shift type and role                                                                                         | Successful creation of employee in the db                    |
| 12  | NC           | Employee can login                                      | Employee account created | Employee username and password                                                                                                      | Successful login                                             |
| 13  | NC           | Rules can be edited                                     | Rules pre created        | Change Mandatory Off Days from 3 to 2 and Save                                                                                      | Successfully changed off days                                |
| 14  | EC           | While editing rules the action can be cancelled         | Rules pre created        | Change Mandatory Off Days from 3 to 2 and Cancel                                                                                    | Successfully changed off days*                               |
| 15  | NC           | User can access the website in english and in german    | User logged in           | Change language through sidebar on the right                                                                                        | Successfully changed language                                |

# <a name="Manual-System-Test-#3">Manual System Test #3</a>

| Nr. | NC / EC / SC | Description                                                                              | Prerequisite                                     | Input                                                                              | Expected Outcome                                                                                    |
|-----|--------------|------------------------------------------------------------------------------------------|--------------------------------------------------|------------------------------------------------------------------------------------|-----------------------------------------------------------------------------------------------------|
| 1   | NC           | DM can create plan for July - next month.                                                | DM logged in                                     | DM username and password & click on "Create Plan for July" button                  | Successful creation of plan                                                                         |
| 2   | NC           | Dm can publish the created plan.                                                         | DM logged in                                     | Click on "Publish" button                                                          | Successful published plan and user of team can see the plan                                         |
| 3   | NC           | Employee can create PDF of the plan                                                      | Employee logged in                               | Click on "Get PDF" button                                                          | Successfully downloaded plan PDF                                                                    |
| 4   | NC           | Employee can choose a third preferred off day.                                           | Employee logged in                               | Click on datepicker and choose one date that is available                          | Successfully saved third off day                                                                    |
| 5   | EC           | Employee can request a holiday if there is a holiday for the same date already requested | Employee logged in                               | Click on datepicker and choose two dates for a time span                           | Successfully saved holiday request*                                                                 |
| 6   | EC           | Employee can request a holiday if there is a off day of preference already chosen        | Off Day for same day preferred                   | Click on datepicker and choose two dates that are during the off day of preference | Successfully saved holiday request*                                                                 |
| 7   | NC           | Employee can edit a holiday request if not already approved/rejected                     | Holiday request saved                            | Click on request and on "Edit" button                                              | Successfully edited and saved request                                                               |
| 8   | NC           | DM can approve holiday requested holiday                                                 | DM logged in                                     | Click on request and on "Approve" button                                           | Successfully approved holiday request and user can see that it is approved                          |
| 9   | NC           | DM can add shifts manually to created plan                                               | DM logged in & plan created                      | Click on day, choose one Shift Type & click on "Create Shift" button               | Successfully saved new shift and user can see it in his schedule                                    |
| 10  | NC           | DM can edit rules                                                                        | DM logged in                                     | Click on a rule and change one parameter and save                                  | Successfully saved the updated rule                                                                 |
| 11  | NC           | DM can mark employee's shift as Sick Leave and set another employee as replacement       | DM logged in & plan created                      | Click on one shift and click on "Sick Leave" then choose one replacement           | Successfully marked shift as sick leave and the replacement can see their new shift in the schedule |
| 12  | NC           | Employees can create shift swap offers                                                   | Employee logged in & has shifts of current month | Click on datepicker and choose one date that has a shift                           | Successfully marked shift as offered and other employees can see it now                             |


*this is the case that should not happen
