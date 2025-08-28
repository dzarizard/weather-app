package com.dzaro.weather_adapter

import com.dzaro.weather_adapter.model.WeatherDto
import com.dzaro.weather_adapter.service.AdapterServiceImpl
import com.github.tomakehurst.wiremock.WireMockServer
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification
import spock.lang.AutoCleanup

import static com.github.tomakehurst.wiremock.client.WireMock.*

class AdapterServiceImplWireMockSpec extends Specification {

    @AutoCleanup
    WireMockServer wm = new WireMockServer(0)

    AdapterServiceImpl service

    def setup() {
        wm.start()
        String baseUrl = "http://localhost:${wm.port()}"
        service = new AdapterServiceImpl(new RestTemplate(), baseUrl, "API_KEY")
    }

    def "getWeather return dto"() {
        given:
        wm.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("Paris"))
                .withQueryParam("appid", matching(".*"))
                .willReturn(okJson('{"name":"Paris","main":{"temp":18.5},"weather":[{"description":"Clouds"}]}')))

        when:
        WeatherDto dto = service.getWeather("Paris")

        then:
        dto.city == "Paris"
        dto.temperature == 18.5d
        dto.description == "Clouds"
    }

    def "getWeather throws HttpClientErrorException"() {
        given:
        wm.stubFor(get(urlPathEqualTo("/data/2.5/weather"))
                .withQueryParam("q", equalTo("Nowhere"))
                .willReturn(aResponse().withStatus(404)))

        when:
        service.getWeather("Nowhere")

        then:
        thrown(HttpClientErrorException)
    }
}

