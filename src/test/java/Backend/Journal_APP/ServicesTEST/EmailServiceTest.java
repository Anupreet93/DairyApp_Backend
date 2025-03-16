package Backend.Journal_APP.ServicesTEST;

import Backend.Journal_APP.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class EmailServiceTest {
    @Autowired
    EmailService emailService;
   // @Test
    void  sendEmail(){
        emailService.sendMail("timetable9393@gmail.com","Testing Javamail","Learning Springboot");
    }
}
