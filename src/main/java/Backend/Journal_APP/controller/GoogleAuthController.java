package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import Backend.Journal_APP.service.UserDetailServiceImpl;
import Backend.Journal_APP.utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseCookie;

import java.util.*;

@RestController
@RequestMapping("/auth/google")
@Slf4j
public class GoogleAuthController {

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    // Redirect URI must match the one configured in your OAuth2 settings.
    private final String redirectUri = "http://localhost:5173/login/auth/google/callback";

    private final RestTemplate restTemplate;
    private final UserDetailServiceImpl userDetailsService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;

    public GoogleAuthController(RestTemplate restTemplate,
                                UserDetailServiceImpl userDetailsService,
                                PasswordEncoder passwordEncoder,
                                UserRepository userRepository,
                                JwtUtil jwtUtil) {
        this.restTemplate = restTemplate;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping("/callback")
    public ResponseEntity<?> handleGoogleCallback(@RequestParam String code) {
        try {
            // 1. Exchange the authorization code for tokens.
            Map<String, Object> tokenResponse = exchangeCodeForTokens(code);

            // 2. Retrieve user information using the id_token.
            Map<String, Object> userInfo = getUserInfo((String) tokenResponse.get("id_token"));

            // 3. Process the user info (lookup or create new user).
            User user = processUserInfo(userInfo);

            // 4. Generate a JWT for local authentication.
            String jwtToken = jwtUtil.generateToken(user.getEmail());

            // 5. Build and return the response with a secure HTTP-only cookie.
            return buildJwtResponse(jwtToken);

        } catch (Exception e) {
            log.error("Google OAuth error: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Authentication failed"));
        }
    }

    private Map<String, Object> exchangeCodeForTokens(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("code", code);
        params.add("client_id", clientId);
        params.add("client_secret", clientSecret);
        params.add("redirect_uri", redirectUri);
        params.add("grant_type", "authorization_code");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(params, headers);

        return restTemplate.postForObject(tokenEndpoint, requestEntity, Map.class);
    }

    private Map<String, Object> getUserInfo(String idToken) {
        // Google provides an endpoint to decode the id_token
        String userInfoUrl = "https://oauth2.googleapis.com/tokeninfo?id_token=" + idToken;
        return restTemplate.getForObject(userInfoUrl, Map.class);
    }

    private User processUserInfo(Map<String, Object> userInfo) {
        String email = (String) userInfo.get("email");
        String name = (String) userInfo.get("name");

        return userRepository.findByEmail(email)
                .orElseGet(() -> createNewUser(email, name));
    }

    private User createNewUser(String email, String name) {
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        // Generate a random password since authentication is handled by Google.
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        newUser.setRoles(Collections.singletonList("USER"));
        return userRepository.save(newUser);
    }

    private ResponseEntity<?> buildJwtResponse(String jwtToken) {
        // Build a secure HTTP-only cookie to store the JWT.
        ResponseCookie cookie = ResponseCookie.from("jwt", jwtToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(3600)   // Cookie valid for 1 hour
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Collections.singletonMap("message", "Authentication successful"));
    }
}
