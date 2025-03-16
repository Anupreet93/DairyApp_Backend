package repositoryTest;

import Backend.Journal_APP.JournalAppApplication;
import Backend.Journal_APP.repository.UserRepositoryImpl;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


//@SpringBootTest(classes = JournalAppApplication.class)

public class UserRepositoryImplTest {
    @Autowired
    UserRepositoryImpl userRepository;
  //./  @Test
    public void checkSA(){
   Assertions.assertNotNull(userRepository.getUser_forSA());
    }
}
