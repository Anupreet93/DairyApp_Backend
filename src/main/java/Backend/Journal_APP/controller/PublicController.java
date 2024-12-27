package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.service.UserEntry_Services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    UserEntry_Services userEntryServices;
    @GetMapping("/health-check")
    public String healthCheck(){
        return "OK";
    }
    @PostMapping("create-user")
    public void create_user(@RequestBody User user){
        userEntryServices.new_saveEntry(user);
    }
}
