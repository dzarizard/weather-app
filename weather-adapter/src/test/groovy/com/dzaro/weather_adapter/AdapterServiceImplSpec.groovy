package com.dzaro.weather_adapter

import com.dzaro.weather_adapter.model.WeatherDto
import com.dzaro.weather_adapter.service.AdapterServiceImpl
import spock.lang.Specification

class AdapterServiceImplSpec extends Specification {

    def service = new AdapterServiceImpl()

    def "should return weather data for valid city"() {
        //given
        String city = "Krakow";

        //when
        WeatherDto result = service.getWeather(city)

        //then
        expect:
        result != null
        result.city == "Krakow"
        result.temperature != null
        result.description != null
    }

    def "should throw exception when city is null or empty"() {
        //when
        service.getWeather("")

        //then
        expect:
        thrown(IllegalArgumentException)
    }
}
