package Backend.Journal_APP.schedular;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

//@SpringBootTest
public class UserSchedularTest {
    @Autowired
    UserScheduler userScheduler;

    public void fetchUsersAndSendMailOfSA(){
       userScheduler.fetchUsersAndSendSaMail();
    }
}
