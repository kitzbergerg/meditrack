# MediTrack

## Setup

Run `docker compose --profile keycloak --profile backend --profile frontend build` to build the docker images.  
Run `docker compose --profile keycloak --profile backend --profile frontend up -d` to start all containers.  
Run `docker compose --profile keycloak --profile backend --profile frontend down` to stop all containers.

The frontend can be accessed at http://localhost:4200/.  
The backend can be accessed at http://localhost:8081/.  
The backend's API documentation can be accessed at http://localhost:8081/swagger-ui/index.html.

Depending on which services you want to run you can leave out profiles. For example to run the backend locally and the
rest in docker do: `docker compose --profile keycloak --profile frontend up -d`

To insert sample data into the database, you can use the backend with the PROFILE env variable: `PROFILE=generate-data docker compose ...`  
You can also start the application with `mvn spring-boot:run -Pgenerate-data`.

## Development Environment

Checkstyle (Sun) is implemented and runs with every build. Initially only as information during the setup phase,
but later the build should fail as a result. The results can be found in the`target/checkstyle-result.xml` file.
You can integrate Checkstyle directly into your IDE by downloading the plugin (in IntelliJ) and configuring it as
follows:

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

We differentiate between feature and bugfix branches. The structure is as follows:
`feature|fix/#<issuenumber>-<some-short-description>`, where the description is optional. For
example, when a new feature is implemented: `feature/#13-backend-setup`. This can be used for the majority of issues.
If bugs are detected (after an issue has been completed!) a new issue must be created and a branch defined as follows:
`fix/#xx-<some-short-description>`, where `xx` is the new created issue number and optional a short description.

### Commits

Commits must follow the following structure:
`#<issue-number>: <commit-message>`
For example: `#13: my meaningful commit message`

## Security

The project uses the SpotBugs plugin with the FindBugs plugin to check for security vulnerabilities.
You can check for security vulnerabilities by running `mvn clean compile spotbugs:spotbugs` in the backend directory
of the project. The results can be found in the `target/spotbugsXml.xml` file.

## OAuth2

The following configuration can be used for authentication:

```json
{
  "auth_url": "http://localhost:8080/realms/meditrack/protocol/openid-connect/auth",
  "token_url": "http://localhost:8080/realms/meditrack/protocol/openid-connect/token",
  "callback_url": "http://localhost:4200/",
  "client_id": "web",
  "grant_type": "code"
}
```

To make calls to the backend you can use Postman with `OAuth 2.0`.
Set `Grant type` to `Authorization Code (With PKCE)` and enter the above values.
Once you click `Get New Access Token` a window should open where you can enter credentials.
The default credentials are username `admin` and password `admin`.
