package com.assignment.spring.service;

import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.entity.Weather;
import com.assignment.spring.persistance.WeatherRepository;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.FieldSetter;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import static com.assignment.spring.DataUtil.*;
import static org.assertj.core.api.Assertions.assertThat;

public class WeatherServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @Mock
    private WeatherRepository weatherRepository;


    @Test
    public void shouldReturnWeather ()  throws Exception {

        MockitoAnnotations.initMocks(this);

        WeatherResponse mockResponse = getMockWeatherResponseKiev();
        Weather expectedWeather = getWeather(mockResponse);

        WeatherService weatherService = new WeatherService(restTemplate, weatherRepository,  new ModelMapper());

        FieldSetter.setField(weatherService, weatherService.getClass().getDeclaredField("weatherApiUrl"),
                "http://api.openweathermap.org/data/2.5/weather");

        FieldSetter.setField(weatherService, weatherService.getClass().getDeclaredField("appId"),
                "60fe381712f5f724485998518f942e79");

        Mockito.when(restTemplate
                .getForEntity("http://api.openweathermap.org/data/2.5/weather?q=Kiev&APPID=60fe381712f5f724485998518f942e79",
                        WeatherResponse.class)).thenReturn(new ResponseEntity<>(mockResponse, HttpStatus.OK));

        Weather actualWeather = weatherService.getRemoteWeather("Kiev");

        assertThat(actualWeather).isEqualTo(expectedWeather);

    }

    @Test
    public void shouldSaveWeather () {
        MockitoAnnotations.initMocks(this);

        WeatherResponse mockResponse = getMockWeatherResponseKiev();
        Weather expectedWeather = getWeather(mockResponse);

        WeatherService weatherService = new WeatherService(restTemplate, weatherRepository,  new ModelMapper());

        weatherService.save(expectedWeather);

        Mockito.verify(weatherRepository, Mockito.times(1)).save(expectedWeather);

    }



}
