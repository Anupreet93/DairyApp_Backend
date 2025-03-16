package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.service.UserDetailServiceImpl;
import Backend.Journal_APP.service.UserEntry_Services;
import Backend.Journal_APP.utility.JwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/public")
@Slf4j
@CrossOrigin(origins = "http://localhost:5173")
public class PublicController {

    @Autowired
    private UserEntry_Services userEntryServices;

    @Autowired
    private UserDetailServiceImpl userDetailService;

    @Autowired
    private JwtUtil jwt_utility;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/health-check")
    public String healthCheck() {
        return "OK";
    }

    @PostMapping("/sign-up")
    public void create_user(@RequestBody User user){
        userEntryServices.new_saveEntry(user);
    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword()));

            UserDetails userDetails = userDetailService.loadUserByUsername(user.getUsername());
            String jwt = jwt_utility.generateToken(userDetails.getUsername());

            // ✅ Return token inside a JSON object
            return ResponseEntity.ok(Map.of("token", jwt));
        } catch (Exception e) {
            log.error("Exception occurred while creating authentication token", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Incorrect username or password"));
        }
    }

}
