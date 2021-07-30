package com.assignment.spring.service;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.entity.Weather;
import com.assignment.spring.exceptions.CityNotFoundException;
import com.assignment.spring.persistance.WeatherRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * Set of weather operations.
 */
@Service
public class WeatherService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherService.class);

    private static final String Q_PARAM = "q";
    private static final String APPID_PARAM = "APPID";

    @Value("${weather.api.url}")
    private String weatherApiUrl;

    @Value("${app.id}")
    private String appId;

    private final RestTemplate restTemplate;

    private final WeatherRepository weatherRepository;

    private final ModelMapper modelMapper;

    @Autowired
    public WeatherService(RestTemplate restTemplate, WeatherRepository weatherRepository, ModelMapper modelMapper) {
        this.restTemplate = restTemplate;
        this.weatherRepository = weatherRepository;
        this.modelMapper = modelMapper;
    }

    /**
     * Calls a remote service to get weather information by city name. Throws CityNotFoundException in case city
     * not found.
     *
     * @param city String city name
     * @return Weather object
     */
    public Weather getRemoteWeather(String city) {
        LOGGER.info("Call {} service", weatherApiUrl);
        final String url = UriComponentsBuilder.fromHttpUrl(weatherApiUrl)
                .queryParam(Q_PARAM, city)
                .queryParam(APPID_PARAM, appId)
                .build().toString();
        ResponseEntity<WeatherResponse> responseEntity = restTemplate.getForEntity(url, WeatherResponse.class);
        if (HttpStatus.NOT_FOUND == responseEntity.getStatusCode()) {
            LOGGER.error("Weather not found for the specific city {}.", city);
            throw new CityNotFoundException();
        }
        return mapper(responseEntity.getBody());
    }

    /**
     * Saves specific Weather object into database
     * @param weather Weather object
     */
    public void save(Weather weather) {
        LOGGER.info("Save weather for {} city", weather.getCity());
        weatherRepository.save(weather);
    }

    private Weather mapper(WeatherResponse response) {
        return modelMapper.typeMap(WeatherResponse.class, Weather.class)
                .addMapping(src -> src.getName(), Weather::setCity)
                .addMapping(src -> src.getSys().getCountry(), Weather::setCountry)
                .addMapping(src -> src.getMain().getTemp(), Weather::setTemperature)
                .map(response);
    }

}
