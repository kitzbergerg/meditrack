import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
  // URL of the Identity Provider's authorization endpoint
  issuer: 'http://localhost:8080/realms/meditrack',

  // URL of the token endpoint to exchange the authorization code for tokens
  tokenEndpoint: 'http://localhost:8080/realms/meditrack/protocol/openid-connect/token',

  // URL of the SPA to redirect the user to after login
  redirectUri: 'http://localhost:4200/test',

  // The client ID registered with the identity provider
  clientId: 'web',

  // The grant type to use for authentication (e.g., authorization code)
  responseType: 'code',

  scope: 'openid',

};
