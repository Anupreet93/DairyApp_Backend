package Backend.Journal_APP.service;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
@Slf4j
@Component
public class UserEntry_Services {
@Autowired
   private UserRepository userRepository;
private static final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

 public void saveEntry(User user){
     userRepository.save(user);
 }
    public void new_saveEntry(User user){
     try {
         user.setPassword(passwordEncoder.encode(user.getPassword()));
         user.setRoles(Arrays.asList("USER"));
         userRepository.save(user);
     }catch (Exception e){
         log.info("nfvifnvin {}",user.getUsername(),e);
         log.error("nfvifnvin");
         log.warn("nfvifnvin");
         log.trace("nfvifnvin");
         log.debug("nfvifnvin");
     }
    }
 public List<User> getAll(){
    return userRepository.findAll();
 }
 public Optional<User> findById(ObjectId id){
     return userRepository.findById(id);
 }
 public void delete_ById(ObjectId id){
     userRepository.deleteById(id);
 }
 public User findByUserName(String username){
     return userRepository.findByUsername(username);
 }



}
