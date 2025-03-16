package Backend.Journal_APP.service;

import Backend.Journal_APP.apiResponce.WeatherResponse;
import Backend.Journal_APP.cache.AppCache;
import Backend.Journal_APP.constants.Placeholders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
@Service
public class WeatherService {
    @Autowired
    AppCache appCache;
    @Autowired
    Placeholders placeholders;
    @Autowired
    RestTemplate restTemplate;
    @Value("${weather.api.key}")
    private  String apikey;
    public WeatherResponse getWeather(String city){
       String final_api = appCache.AppCache.get("WEATHER_API").replace(placeholders.City,city).replace(placeholders.API_KEY,apikey);
        ResponseEntity<WeatherResponse> response = restTemplate.exchange(final_api, HttpMethod.GET, null, WeatherResponse.class);
        return response.getBody();
    }
}
