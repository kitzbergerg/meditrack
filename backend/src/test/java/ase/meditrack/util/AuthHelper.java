package ase.meditrack.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

public class AuthHelper {

    public static String obtainAccessToken(String username, String password) {
        RestTemplate restTemplate = new RestTemplate();
        String authUrl = "http://localhost:8080/realms/meditrack/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", "testclient");
        map.add("username", username);
        map.add("password", password);
        map.add("grant_type", "password");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, headers);

        String response = restTemplate.exchange(authUrl, HttpMethod.POST, request, String.class).getBody();

        // Parse the response to extract the access token
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode jsonNode = mapper.readTree(response);
            return jsonNode.path("access_token").asText();
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse access token from response");
        }
    }
}
