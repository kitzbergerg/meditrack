## Component diagram

```mermaid
graph TB
    User <--> Frontend
    Frontend <--> Backend
    Backend <--> Postgresql
    Backend -- token validation --> Keycloak
    Frontend -- login --> Keycloak
```

## Auth diagram

This diagram shows how the system handles authorization using Keycloak.

You can find an in-depth description of the authorization code flow [here](https://auth0.com/docs/get-started/authentication-and-authorization-flow/authorization-code-flow) (i.e. of the interaction between user, frontend and Keycloak).


```mermaid
sequenceDiagram
    User ->> Frontend: Login
    activate Frontend
    Frontend ->> Keycloak: Redirect to Keycloak login
    deactivate Frontend
    activate Keycloak
    User ->> Keycloak: Enter credentials
    Keycloak -->> Frontend: Redirect back with authorization code
    deactivate Keycloak
    activate Frontend
    Frontend ->> Keycloak: Exchange authorization code for tokens
    deactivate Frontend
    activate Frontend
    par
        Frontend ->> Keycloak: Occasionally refresh access token
    and
        User ->> Frontend: Interact
        activate Frontend
        Frontend ->> Backend: API requests with JWT
        activate Backend
        opt initially or on key rotation
            Backend ->> Keycloak: Fetch JWKs to validate JWT
        end
        Backend ->> Backend: Validate token using JWKs
        Backend -->> Frontend: API response
        deactivate Backend
        deactivate Frontend
    end
```