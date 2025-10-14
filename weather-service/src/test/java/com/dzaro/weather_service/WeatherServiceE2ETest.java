package com.dzaro.weather_service;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class WeatherServiceE2ETest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private static WireMockServer wm;
    private static Path dumpDir;

    @BeforeAll
    static void init() throws Exception {
        wm = new WireMockServer(wireMockConfig().dynamicPort());
        wm.start();
        dumpDir = Files.createTempDirectory("weather-dumps-");
    }

    @AfterAll
    static void stop() {
        if (wm != null) wm.stop();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("adapter.base-url", () -> wm.baseUrl());
        r.add("dump.folder", () -> dumpDir.toAbsolutePath().toString());
    }

    private String baseUrl() {
        return "http://localhost:" + port;
    }

    @Test
    void getWeather_returns200_andDto_fromAdapterStub() {
        wm.stubFor(get(urlPathEqualTo("/adapter/weather"))
                .withQueryParam("city", equalTo("Krakow"))
                .willReturn(okJson("""
                    { "city":"Krakow", "temperature":20.5, "description":"Sunny" }
                """)));

        ResponseEntity<Map> resp = rest.getForEntity(
                baseUrl() + "/weather?city=Krakow", Map.class);

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(resp.getBody()).isNotNull();
        assertThat(resp.getBody().get("city")).isEqualTo("Krakow");
        assertThat(resp.getBody().get("temperature")).isEqualTo(20.5);
        assertThat(resp.getBody().get("description")).isEqualTo("Sunny");
    }

    @Test
    void history_returnsFilteredByCity_afterCallingWeatherTwice() {
        wm.stubFor(get(urlPathEqualTo("/adapter/weather"))
                .withQueryParam("city", equalTo("Paris"))
                .willReturn(okJson("""
                    { "city":"Paris", "temperature":18.0, "description":"Clouds" }
                """)));
        wm.stubFor(get(urlPathEqualTo("/adapter/weather"))
                .withQueryParam("city", equalTo("Berlin"))
                .willReturn(okJson("""
                    { "city":"Berlin", "temperature":16.0, "description":"Rain" }
                """)));

        assertThat(rest.getForEntity(baseUrl()+"/weather?city=Paris", Map.class)
                .getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(rest.getForEntity(baseUrl()+"/weather?city=Berlin", Map.class)
                .getStatusCode()).isEqualTo(HttpStatus.OK);

        ResponseEntity<List> hist = rest.getForEntity(
                baseUrl()+"/history?city=Paris", List.class);

        assertThat(hist.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(hist.getBody()).isNotNull();
        assertThat(hist.getBody()).isNotEmpty();
        Map first = (Map) hist.getBody().getFirst();
        assertThat(first.get("city")).isEqualTo("Paris");
    }

//    @Test
//    void dump_returns202_andCreatesFile() throws Exception {
//        wm.stubFor(get(urlPathEqualTo("/adapter/weather"))
//                .withQueryParam("city", equalTo("Gdansk"))
//                .willReturn(okJson("""
//                    { "city":"Gdansk", "temperature":15.0, "description":"Wind" }
//                """)));
//        rest.getForEntity(baseUrl()+"/weather?city=Gdansk", Map.class);
//
//        ResponseEntity<Map> dumpResp = rest.postForEntity(
//                baseUrl()+"/dump", HttpEntity.EMPTY, Map.class);
//
//        assertThat(dumpResp.getStatusCode()).isEqualTo(HttpStatus.ACCEPTED);
//        assertThat(dumpResp.getBody()).isNotNull();
//        Object requestId = dumpResp.getBody().get("requestId");
//        assertThat(requestId).isNotNull();
//
//        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
//            try (var stream = Files.list(dumpDir)) {
//                assertThat(stream.anyMatch(p -> p.getFileName().toString().contains(requestId.toString()))).isTrue();
//            }
//        });
//    }
}
