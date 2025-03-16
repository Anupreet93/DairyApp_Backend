package Backend.Journal_APP.controller;

import Backend.Journal_APP.cache.AppCache;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import Backend.Journal_APP.service.UserEntry_Services;
import ch.qos.logback.classic.helpers.MDCInsertingServletFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminController {
    private final UserEntry_Services userEntryServices;
    @Autowired
    AppCache appCache;

    public AdminController(UserEntry_Services userEntryServices) {
        this.userEntryServices = userEntryServices;
    }
@Autowired
    UserRepository userRepository;
    @GetMapping("/all-users")
    public ResponseEntity<?> get_all() {
        List<User> all = userEntryServices.getAll();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/delete-users")
    public ResponseEntity<?> delete_all() {
         userRepository.deleteAll();

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    @GetMapping("/clear-all-cache")
    public void clearCache(){
        appCache.init();
    }
}
