package Backend.Journal_APP.controller;

import Backend.Journal_APP.entity.JournalEntry;
import Backend.Journal_APP.entity.User;
import Backend.Journal_APP.enums.Sentiment;
import Backend.Journal_APP.repository.JournalRepository;
import Backend.Journal_APP.service.JournalEntry_Services;
import Backend.Journal_APP.service.UserEntry_Services;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/journal")
@CrossOrigin(origins = "http://localhost:5173")
public class JournalEntry_Controller {

    @Autowired
    private JournalEntry_Services journalEntryService;

    @Autowired
    private UserEntry_Services userService;

    @Autowired
    private JournalRepository journalRepository;

    // ✅ Fetch all journal entries for the authenticated user
    @GetMapping
    public ResponseEntity<?> getAllJournalEntriesOfUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            System.out.println("Fetching journal entries for user: " + userName);

            User user = userService.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
            }

            List<JournalEntry> all = user.getJournalEntries();
            return all.isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).body("No journal entries found.")
                    : ResponseEntity.ok(all);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to retrieve journal entries.");
        }
    }

    // ✅ Create a new journal entry
    @PostMapping
    public ResponseEntity<?> createEntry(@RequestBody JournalEntry myEntry) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            System.out.println("Creating entry for user: " + userName);

            User user = userService.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
            }

            // Set default values
            myEntry.setDate(LocalDateTime.now());
            if (myEntry.getSentiment() == null) {
                myEntry.setSentiment(Sentiment.NEUTRAL);
            }

            journalEntryService.saveEntry(myEntry, userName);
            return ResponseEntity.status(HttpStatus.CREATED).body(myEntry);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error creating journal entry: " + e.getMessage());
        }
    }

    // ✅ Get journal entry by ID
    @GetMapping("/{myId}")
    public ResponseEntity<?> getJournalEntryById(@PathVariable ObjectId myId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String userName = authentication.getName();
            System.out.println("Fetching journal entry with ID: " + myId + " for user: " + userName);

            User user = userService.findByUserName(userName);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
            }

            Optional<JournalEntry> journalEntry = journalEntryService.findById(myId);
            return journalEntry.map(ResponseEntity::ok)
                    .orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body(null));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching journal entry.");
        }
    }

    // ✅ Delete a journal entry by ID
    @DeleteMapping("/{username}/{myId}")
    public ResponseEntity<String> deleteById(
            @PathVariable String myId, // Accept ID as String
            @PathVariable String username
    ) {
        try {
            // Convert String to ObjectId (throws IllegalArgumentException if invalid)
            ObjectId objectId = new ObjectId(myId);

            // Get the authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();

            // Check if the authenticated user matches the requested username
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to delete this entry.");
            }

            // Find the entry by ID
            Optional<JournalEntry> entry = journalRepository.findById(objectId);

            if (entry.isPresent()) {
                // Delete the entry
                journalRepository.deleteById(objectId);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("Journal entry deleted successfully.");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found.");
            }
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid journal entry ID format.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting journal entry.");
        }
    }

    // ✅ Delete all journal entries of the authenticated user
    @DeleteMapping("/delete-all")
    public ResponseEntity<?> deleteAllJournalEntries() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String username = authentication.getName();
            System.out.println("Deleting all journal entries for user: " + username);

            User user = userService.findByUserName(username);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
            }

            List<JournalEntry> journalEntries = user.getJournalEntries();
            if (!journalEntries.isEmpty()) {
                journalEntries.forEach(entry -> journalEntryService.deleteById(entry.getId(), username));
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body("All entries deleted successfully.");
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No journal entries found to delete.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error deleting journal entries.");
        }
    }

    // ✅ Update a journal entry
    @PutMapping("{username}/{myId}")
    public ResponseEntity<?> updateJournalById(
            @PathVariable String username,
            @PathVariable ObjectId myId,
            @RequestBody JournalEntry newEntry) {
        try {
            // Get the authenticated user's username
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String authenticatedUsername = authentication.getName();
            System.out.println("Updating journal entry ID: " + myId + " for user: " + authenticatedUsername);

            // Optional: Ensure the URL username matches the authenticated user
            if (!authenticatedUsername.equals(username)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized user.");
            }

            // Fetch the user from the service
            User user = userService.findByUserName(authenticatedUsername);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not found or unauthorized.");
            }

            // Retrieve the existing journal entry by ID
            Optional<JournalEntry> optionalEntry = journalEntryService.findById(myId);
            if (optionalEntry.isPresent()) {
                JournalEntry existingEntry = optionalEntry.get();

                // Update fields if new values are provided
                if (newEntry.getTitle() != null && !newEntry.getTitle().trim().isEmpty()) {
                    existingEntry.setTitle(newEntry.getTitle());
                }
                if (newEntry.getContent() != null && !newEntry.getContent().trim().isEmpty()) {
                    existingEntry.setContent(newEntry.getContent());
                }
                if (newEntry.getSentiment() != null) {
                    existingEntry.setSentiment(newEntry.getSentiment());
                }

                // Save the updated journal entry
                JournalEntry updatedEntry = journalEntryService.saveEntry2(existingEntry);
                return ResponseEntity.ok(updatedEntry);
            }

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Journal entry not found.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error updating journal entry.");
        }
    }

}
