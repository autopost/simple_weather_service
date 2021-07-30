package com.assignment.spring;

import com.assignment.spring.api.Main;
import com.assignment.spring.api.Sys;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.entity.Weather;

public class DataUtil {

    public static  Weather getWeather(WeatherResponse mockResponse) {
        Weather weather = new Weather();
        weather.setId(mockResponse.getId());
        weather.setCity(mockResponse.getName());
        weather.setCountry(mockResponse.getSys().getCountry());
        weather.setTemperature(mockResponse.getMain().getTemp());
        return weather;
    }

    public static  WeatherResponse getMockWeatherResponseKiev () {
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setName("Kiev");
        mockResponse.setId(204655);

        Sys sys = new Sys();
        sys.setCountry("UA");
        mockResponse.setSys(sys);

        Main main = new Main();
        main.setTemp(202.23);
        mockResponse.setMain(main);

        return mockResponse;
    }

    public static  WeatherResponse getMockWeatherResponseBerlin () {
        WeatherResponse mockResponse = new WeatherResponse();
        mockResponse.setName("Berlin");
        mockResponse.setId(204654);

        Sys sys = new Sys();
        sys.setCountry("DE");
        mockResponse.setSys(sys);

        Main main = new Main();
        main.setTemp(202.23);
        mockResponse.setMain(main);
        return mockResponse;
    }
}
