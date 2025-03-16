package Backend.Journal_APP.controller;

import Backend.Journal_APP.apiResponce.WeatherResponse;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import Backend.Journal_APP.service.UserEntry_Services;
import Backend.Journal_APP.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "http://localhost:5173")
public class UserEntry_Controller {
   @Autowired
    UserEntry_Services userEntryServices;
   @Autowired
    UserRepository userRepository;
   @Autowired
    WeatherService weatherService;
//   @GetMapping
//    public List<User> getAll(){
//       return userEntryServices.getAll();
//   }
@GetMapping("/me")
public ResponseEntity<?> getLoggedInUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    if (authentication == null || !authentication.isAuthenticated()) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }

    Object principal = authentication.getPrincipal();
    if (principal instanceof UserDetails) {
        String username = ((UserDetails) principal).getUsername();
        User user = userEntryServices.findByUserName(username);

        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
        }

        return ResponseEntity.ok(user);
    }

    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid authentication");
}

   @PutMapping
    public ResponseEntity<?> update_user(@RequestBody User user){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       String username = authentication.getName();
       User userInDB = userEntryServices.findByUserName(username);
           userInDB.setUsername(user.getUsername());
           userInDB.setPassword(user.getPassword());
           userEntryServices.new_saveEntry(userInDB);
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   }
   @DeleteMapping
    public ResponseEntity<?> Delete_By_username(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       userRepository.deleteByUsername(authentication.getName());
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   }
      @GetMapping
    public ResponseEntity<?> Greetings(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
          WeatherResponse weatherResponse = weatherService.getWeather("Mumbai");
        String greetings = "";
        if(weatherResponse != null){
            greetings = " Weather feels like "+weatherResponse.getCurrent().getFeelslike();
        }
        return new ResponseEntity<>("Hi "+ authentication.getName() + greetings,HttpStatus.OK);

    }
}
