package Backend.Journal_APP.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

public class WeatherService {
    @Autowired
    RestTemplate restTemplate;
    private final String apikey = "227142e889588c554c2a9a43d8277da8";
    private final String API = "http://api.weatherstack.com/current?access_key=API_KEY&query=CITY";
    private void getWeather(String city){
       String final_api = API.replace("CITY",city).replace("API_KEY",apikey);
       restTemplate.exchange(final_api, HttpMethod.GET,null,)
    }
}
