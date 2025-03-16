package Backend.Journal_APP.service;

import Backend.Journal_APP.entity.JournalEntry;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.repository.JournalRepository;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class JournalEntry_Services {

    private static final Logger logger = Logger.getLogger(JournalEntry_Services.class.getName());

    @Autowired
    private JournalRepository journalRepository;

    @Autowired
    private UserEntry_Services userEntryServices;

    // ✅ Save a journal entry for the authenticated user
    @Transactional
    public void saveEntry(JournalEntry journalEntry, String username) {
        try {
            User user = userEntryServices.findByUserName(username);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }

            journalEntry.setDate(LocalDateTime.now());
            journalEntry.setUsername(username); // ✅ Ensure journal entry has username
            JournalEntry savedEntry = journalRepository.save(journalEntry);

            // ✅ Initialize journal list if null
            if (user.getJournalEntries() == null) {
                user.setJournalEntries(new java.util.ArrayList<>());
            }

            user.getJournalEntries().add(savedEntry);
            userEntryServices.saveEntry(user);

            logger.info("Journal entry saved successfully for user: " + username);
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while saving the journal entry", e);
        }
    }

    // ✅ Save existing journal entry (for updates)
    public JournalEntry saveEntry2(JournalEntry journalEntry) {
        journalRepository.save(journalEntry);
        return journalEntry;
    }

    // ✅ Get all journal entries
    public List<JournalEntry> getAll() {
        return journalRepository.findAll();
    }

    // ✅ Find journal entry by ID
    public Optional<JournalEntry> findById(ObjectId id) {
        return journalRepository.findById(id);
    }

    // ✅ Delete journal entry by ID
    @Transactional
    public boolean deleteById(ObjectId id, String username) {
        try {
            User user = userEntryServices.findByUserName(username);
            if (user == null) {
                throw new RuntimeException("User not found: " + username);
            }

            boolean removed = user.getJournalEntries().removeIf(entry -> entry.getId().equals(id));

            if (removed) {
                userEntryServices.saveEntry(user);
                journalRepository.deleteById(id);
                logger.info("Journal entry deleted successfully for user: " + username);
            } else {
                logger.warning("Journal entry not found for deletion: " + id);
            }

            return removed;
        } catch (Exception e) {
            logger.severe("Error deleting journal entry: " + e.getMessage());
            throw new RuntimeException("Error deleting journal entry", e);
        }
    }
}
