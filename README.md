# MediTrack

## Setup

Run `docker compose --profile keycloak --profile backend --profile frontend build` to build the docker images.  
Run `docker compose --profile keycloak --profile backend --profile frontend up -d` to start all containers.  
Run `docker compose --profile keycloak --profile backend --profile frontend down` to stop all containers.

Depending on which services you want to run you can leave out profiles. For example to run the backend locally and the
rest in docker do: `docker compose --profile keycloak --profile frontend up -d`

## Development Environment

Checkstyle (Sun) is implemented and runs with every build. Initially only as information during the setup phase,
but later the build should fail as a result. The results can be found in the`target/checkstyle-result.xml` file.
You can integrate Checkstyle directly into your IDE by downloading the plugin (in IntelliJ) and configuring it as
follows:

- Go to `File` -> `Settings` -> `Tools` -> `Checkstyle`
- Click on the `+` sign and select the `checkstyle.xml` file in the root of the backend directory of the project
- Set scope to `all files` and click `OK`

For the frontend, ESLint is implemented and can be run with `npm run lint`. The results can be found in the console.

## Security

The project uses the SpotBugs plugin with the FindBugs plugin to check for security vulnerabilities.
You can check for security vulnerabilities by running `mvn clean compile spotbugs:spotbugs` in the backend directory
of the project. The results can be found in the `target/spotbugsXml.xml` file.
