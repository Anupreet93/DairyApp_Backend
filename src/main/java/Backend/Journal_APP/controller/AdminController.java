package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.service.UserEntry_Services;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final UserEntry_Services userEntryServices;

    public AdminController(UserEntry_Services userEntryServices) {
        this.userEntryServices = userEntryServices;
    }

    @GetMapping("/all-users")
    public ResponseEntity<?> get_all() {
        List<User> all = userEntryServices.getAll();
        if (all != null && !all.isEmpty()) {
            return new ResponseEntity<>(all, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
