package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import Backend.Journal_APP.service.UserEntry_Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserEntry_Controller {
   @Autowired
    UserEntry_Services userEntryServices;
   @Autowired
    UserRepository userRepository;
   @GetMapping
    public List<User> getAll(){
       return userEntryServices.getAll();
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
    public ResponseEntity<?> delete_by_username(){
       Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
       userRepository.deleteByUsername(authentication.getName());
       return new ResponseEntity<>(HttpStatus.NO_CONTENT);
   }
}
