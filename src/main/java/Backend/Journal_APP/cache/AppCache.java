package Backend.Journal_APP.cache;

import Backend.Journal_APP.entity.ConfigJournalApp;
import Backend.Journal_APP.repository.ConfigJournalAppRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Component
public class AppCache {

    @Autowired
    ConfigJournalAppRepository configJournalAppRepository;
    public Map<String,String> AppCache;
@PostConstruct
    public void init(){
        AppCache = new HashMap<>();
        List<ConfigJournalApp> all =  configJournalAppRepository.findAll();
       for (ConfigJournalApp configJournalApp : all){
           AppCache.put(configJournalApp.getKey(), configJournalApp.getValue());
       }
    }
}
