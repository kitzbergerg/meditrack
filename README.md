# MediTrack

## Setup

Run `docker compose build` to build the docker images.  
Run `docker compose up -d` to start all containers.  
Run `docker compose down` to stop all containers.

## Development Environment
Checkstyle (Sun) is implemented and runs with every build. Initially only as information during the setup phase, 
but later the build should fail as a result. The results can be found in the`target/checkstyle-result.xml` file.
You can integrate Checkstyle directly into your IDE by downloading the plugin (in IntelliJ) and configuring it as follows:
- Go to `File` -> `Settings` -> `Tools` -> `Checkstyle`
- Click on the `+` sign and select the `checkstyle.xml` file in the root of the backend directory of the project
- Set scope to `all files` and click `OK`

For the frontend, ESLint is implemented and can be run with `npm run lint`. The results can be found in the console.

## Guidelines
In order to guarantee a consistent code base and repo structure, the coding conventions must be implemented. 
It can be helpful to use Checkstyle directly in the IDE. You can find out how this can be set up 
in the Development Environment section above.

## Conventions
### Code
#### Java
We use the Sun coding conventions. The general checks can be found [here](https://checkstyle.sourceforge.io/checks.html) 
and sun specific styling [here](https://checkstyle.sourceforge.io/sun_style.html). However, it is sufficient to simply 
perform a build, as this automatically checks the rules and displays any failures in the console.

#### TypeScript
To follow Angular and TypeScript best practices, we use the `Recommended` set of ESLint rules. This can be viewed 
[here](https://github.com/angular-eslint/angular-eslint/blob/main/packages/eslint-plugin/src/configs/README.md).

### Branches
We differentiate between feature and bugfix branches. The structure is as follows: `feature|fix: #<issuenumber>`. For 
example, when a new feature is implemented: `feature: #13`. This can be used for the majority of issues. If bugs are 
detected (after an issue has been completed!) a new issue must be created and a branch defined as follows: 
`fix: #xx`, where `xx` is the new created issue number.

### Commits
Depending on the branch (feature or bugfix), commits must follow the following structure:
`feature|fix: #<issue-number>: <commit-message>`
For example, if I am on a feature branch: `feature: #13: my meaningful commit message`

## Security
The project uses the SpotBugs plugin with the FindBugs plugin to check for security vulnerabilities.
You can check for security vulnerabilities by running `mvn clean compile spotbugs:spotbugs` in the backend directory 
of the project. The results can be found in the `target/spotbugsXml.xml` file.
