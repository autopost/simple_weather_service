package com.assignment.spring.rest;

import com.assignment.spring.entity.Weather;
import com.assignment.spring.service.WeatherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotEmpty;


@RestController
@Validated
public class WeatherController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WeatherController.class);

    private WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @Operation(summary = "Get a weather by city")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found the weather",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Weather.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid city",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Weather not found",
                    content = @Content)})
    @GetMapping("/weather")
    public Weather weather(@RequestParam(value = "city") @NotEmpty String city) {
        LOGGER.info("Requested weather for {}", city);
        final Weather weather = weatherService.getRemoteWeather(city);
        weatherService.save(weather);
        return weather;
    }

}
