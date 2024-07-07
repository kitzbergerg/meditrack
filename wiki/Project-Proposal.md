## Team Members

| Name               | Matr. Nr. | E-Mail                            | Primary Role         | Secondary Role    |
|--------------------|-----------|-----------------------------------|----------------------|-------------------|
| Cobzaru Luca Marius | 11712226 | e11712226@student.tuwien.ac.at | Frontend UI/UX       | Tester            |
| Grassauer Alina    | 11905176  | alina.grassauer@tuwien.ac.at     | Team Coordinator     | Frontend UI/UX   |
| Gülmez Gülsüm      | 11905145  | e11905145@student.tuwien.ac.at   | Tester               | Quality Manager  |
| Kitzberger Gabriel | 12024014  | gabriel.kitzberger@student.tuwien.ac.at | Technical Architect | Auth & Security  |
| Petermandl Jakob   | 11776823  | e11776823@student.tuwien.ac.at    | Quality Manager      | Documentation    |
| Wolkersdorfer Lucas | 11922587 | e11922587@student.tuwien.ac.at | Requirements Engineer | Technical Architect |

## Project Description

The aim of this project is to develop a Medical Staff Scheduling Platform designed to optimize and simplify the scheduling processes within healthcare institutions. This platform enables healthcare management staff to automatically schedule employees' work times based on their availability, preferences and other necessities. It addresses unique challenges faced by healthcare organizations like 12 hour shifts, weekend shifts and 24/7 staffing requirements that are usually not built into scheduling software.

**Domain:** Healthcare Technology  
**Problems/Challenges Solved:** This platform solves the inefficient and time intensive work scheduling and offers time management in medical settings. It automatically creates rosters, taking the employees' differing preferences into account.  
**Expected Improvements:** Provide features and customization specific to healthcare that other staff scheduling providers lack.  
**Potential to Replace:** It aims to replace generic scheduling tools that lack the specialized features necessary for medical staff roster planning, by offering a tailored solution that meets the unique demands.

### Features:
- **Team Management:** Employers can create teams and add employees to them.
- **Automatic Scheduling:** This month's roster is created automatically.
- **Preferences and Offdays:** Employees can set preferences for shifts and holidays which are factored in when creating the working schedule.
- **Easy employee signup:** After adding employees, automatically create their accounts and send them a sign in email.
- **Management and Overview:** Employees are able to view the schedule and track their working hours. Employers have an overview with statistics on all employees.

## Architecture

**Type of System:** Web Application  
**Technologies Used:** Frontend (React.js/Angular), Backend (Java with Spring boot), Docker, Keycloak, database (Postgres, maybe Supabase)

## Already existing and similar products

Staff scheduling products do exist, however, they lack important healthcare sector specific features and can be costly.

## Stakeholder

The stakeholders are:
- Hospital/Medical Facility: Customer, who purchase the application
- Department Management/Human Resource Management: Primary user of the application
- Employee: Secondary user; adds personal data/preferences which serves as input for the scheduling algorithm
- Development Team: Responsible for the app's creation and maintenance

## Legal environment

No licenses are used.
The product will be freely available during the course of this LVA.

## Cost estimation (working hours)

150 hours minimum per team member.

## Risks

| Risk                           | Probability | Impact        | Countermeasures                                                  |
|--------------------------------|-------------|---------------|------------------------------------------------------------------|
| Insufficient Design Decisions  | Medium      | High          | Engage all members - Prioritize modularity - Conduct regular design reviews |
| Data Security Breaches         | Low to Medium | High        | Implement robust security measures - Regular security audits - Compliance with data protection regulations (e.g., GDPR) |
| Risk of Delayed Completion     | High        | High          | Regular progress monitoring - Adjust scope/resources - Agile methodologies |
| Performance Issues             | Medium      | Medium to High | Thorough performance testing - Code optimization |
| User Adoption                  | Medium      | Medium        | Develop user-friendly interface - Work with future users during development |
| Scope Creep                    | Medium      | Medium        | Clearly define project scope and requirements - Implement change management processes - Prioritize feature requests |
| Technical Debt                 | Medium      | Medium to High | Allocate time for refactoring - Prioritize technical debt reduction |
