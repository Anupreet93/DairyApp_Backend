package Backend.Journal_APP.service;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserEntry_Services {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public void saveEntry(User user) {
        userRepository.save(user);
    }

    public void new_saveEntry(User user) {
        try {
            user.setPassword(passwordEncoder.encode(user.getPassword())); // Hash password before saving
            user.setRoles(Arrays.asList("USER")); // Assign default role
            userRepository.save(user);
            log.info("User {} registered successfully", user.getUsername());
        } catch (Exception e) {
            log.error("Error while saving user {}: {}", user.getUsername(), e.getMessage(), e);
        }
    }

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(ObjectId id) {
        return userRepository.findById(id);
    }

    public void delete_ById(ObjectId id) {
        userRepository.deleteById(id);
    }

    public User findByUserName(String username) {
        if (username == null || username.trim().isEmpty()) {
            log.error("Username is null or empty");
            return null;
        }

        Optional<User> user = userRepository.findByUsername(username);
        return user.orElse(null);
    }

}
