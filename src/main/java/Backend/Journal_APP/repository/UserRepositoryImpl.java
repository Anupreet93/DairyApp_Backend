package Backend.Journal_APP.repository;

import Backend.Journal_APP.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import java.util.List;

public class UserRepositoryImpl {
    @Autowired
    private MongoTemplate mongoTemplate;
    public List<User> getUser_forSA(){
        Query query = new Query();
        query.addCriteria(Criteria.where("email").regex("^[a-zA-Z0-9._%+-]+@gmail\\.com$"));
        query.addCriteria(Criteria.where("sentimentAnalysis").is(true));
         return mongoTemplate.find(query,User.class);
    }


}
