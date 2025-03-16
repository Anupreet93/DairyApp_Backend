package Backend.Journal_APP.repository;


import Backend.Journal_APP.entity.ConfigJournalApp;
import Backend.Journal_APP.entity.User;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ConfigJournalAppRepository extends MongoRepository<ConfigJournalApp, ObjectId> {


}

