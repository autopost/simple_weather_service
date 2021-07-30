package com.assignment.spring.rest;


import com.assignment.spring.DataUtil;
import com.assignment.spring.api.WeatherResponse;
import com.assignment.spring.persistance.WeatherRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockserver.integration.ClientAndServer;
import org.mockserver.model.Header;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.TimeUnit;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class WeatherControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WeatherRepository weatherRepository;

    private static ClientAndServer mockServer;

    @BeforeClass
    public static void startServer() throws JsonProcessingException {
        mockServer = startClientAndServer(8082);
        WeatherResponse mockResponse = DataUtil.getMockWeatherResponseBerlin();

        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/weather")
                        .withQueryStringParameter("q", "Berlin")
                        .withQueryStringParameter("APPID", "234"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-a1ge=86400"))
                                .withBody(new ObjectMapper().writeValueAsString(mockResponse))
                                .withDelay(TimeUnit.SECONDS, 1)
                );

    }

    @AfterClass
    public static void stopServer() {
        mockServer.stop();
    }

    @After
    public void cleanUpRepository() {
        weatherRepository.deleteAll();
    }

    @Test
    public void shouldReturnWeather_OneCity() throws Exception {


        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(0);

        this.mockMvc.perform(get("/weather")
                .param("city", "Berlin"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json;charset=UTF-8"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("DE"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.temperature").isNumber());

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(1);
    }

    @Test
    public void shouldSaveOneWeather_CallTwoTimes_SameCity() throws Exception {

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(0);

        this.mockMvc.perform(get("/weather")
                .param("city", "Berlin"))
                .andExpect(status().isOk());
        this.mockMvc.perform(get("/weather")
                .param("city", "Berlin"))
                .andExpect(status().isOk());

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(1);
    }

    @Test
    public void shouldSaveTwoWeathers_CallTwoTimes_DifferentCities() throws Exception {

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(0);

        this.mockMvc.perform(get("/weather")
                .param("city", "Berlin"))
                .andExpect(status().isOk());

        WeatherResponse mockResponse = DataUtil.getMockWeatherResponseKiev();
        mockServer.when(
                request()
                        .withMethod("GET")
                        .withPath("/weather")
                        .withQueryStringParameter("q", "Kiev")
                        .withQueryStringParameter("APPID", "234"))
                .respond(
                        response()
                                .withStatusCode(200)
                                .withHeaders(
                                        new Header("Content-Type", "application/json; charset=utf-8"),
                                        new Header("Cache-Control", "public, max-a1ge=86400"))
                                .withBody(new ObjectMapper().writeValueAsString(mockResponse))
                                .withDelay(TimeUnit.SECONDS, 1)
                );
        this.mockMvc.perform(get("/weather")
                .param("city", "Kiev"))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.country").value("UA"));

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(2);
    }

    @Test
    public void shouldReturn404_IncorrectUrl() throws Exception {
        this.mockMvc.perform(get("/weather1"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404_EmptyUrl() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void shouldReturn404_IncorrectCity() throws Exception {
        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(0);

        this.mockMvc.perform(get("/weather")
                .param("city", "Berlin1"))
                .andExpect(status().isNotFound());

        assertThat(StreamSupport.stream(weatherRepository.findAll().spliterator(), false).count()).isEqualTo(0);
    }

    @Test
    public void shouldReturn400_EmptyCity() throws Exception {
        this.mockMvc.perform(get("/weather?city="))
                .andExpect(status().isBadRequest());
    }


}
