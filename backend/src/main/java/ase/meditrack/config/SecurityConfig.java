package ase.meditrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // TODO: set up proper security with keycloak
        http.authorizeHttpRequests(requests -> requests.anyRequest().permitAll());
        http.csrf().disable(); //needed for POST requests, otherwise 403 will automatically be returned
        return http.build();
    }

}
