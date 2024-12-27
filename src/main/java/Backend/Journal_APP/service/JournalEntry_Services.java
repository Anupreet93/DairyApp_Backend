package Backend.Journal_APP.service;

import Backend.Journal_APP.entity.JournalEntry;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class JournalEntry_Services {
@Autowired
   private JournalRepository journalRepository;
@Autowired
private UserEntry_Services userEntryServices;
//@Transactional
 public void saveEntry(JournalEntry journalEntry, String username){
    try {
        User user = userEntryServices.findByUserName(username);
        journalEntry.setDate(LocalDateTime.now());
        JournalEntry saved = journalRepository.save(journalEntry);
        user.getJournalEntries().add(saved);
        userEntryServices.saveEntry(user);
    }
     catch (Exception e){
        throw new RuntimeException("An Error ocurred while saving the entry",e);
     }
 }
    public void saveEntry2(JournalEntry journalEntry){
        journalRepository.save(journalEntry);
    }
 public List<JournalEntry> getAll(){
    return journalRepository.findAll();
 }
 public Optional<JournalEntry> findById(ObjectId id){
     return journalRepository.findById(id);
 }
 public boolean deleteById(ObjectId id ,String username){
     boolean removed = false;
    try {
        User user = userEntryServices.findByUserName(username);
         removed = user.getJournalEntries().removeIf(x -> x.getId().equals(id));
        if (removed){
            userEntryServices.saveEntry(user);
            journalRepository.deleteById(id);
        }
    }catch (Exception e){
        System.out.println(e);
        throw new RuntimeException("Id not found",e);
    }
return removed;
 }


}
