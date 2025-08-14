package com.dzaro.weather_service

import com.dzaro.weather_service.model.DumpAcceptedDto
import com.dzaro.weather_service.model.WeatherDto
import com.dzaro.weather_service.service.WeatherServiceImpl
import spock.lang.Specification

class WeatherServiceImplSpec extends Specification {

    def service = new WeatherServiceImpl()


    def "should return null weather when city is null"() {
        expect:
        service.getWeather(null) == null
    }

    def "getHistory should return empty list"() {
        expect:
        service.getHistory(null, null, null).isEmpty()
    }

    def "getWeather should return WeatherDto with requested city"() {
        when:
        def result = service.getWeather("London")

        then:
        result != null
        result instanceof WeatherDto
        result.city == "London"
        result.temperature != null
    }

    def "getWeather should throw IllegalArgumentException when city is null"() {
        when:
        service.getWeather(null)

        then:
        thrown(IllegalArgumentException)
    }


    def "getHistory should return entries filtered by city"() {
        when:
        def result = service.getHistory("Paris", null, null)

        then:
        result.every { it.city == "Paris" }
        !result.isEmpty()
    }

    def "requestDataDump should return confirmation DTO"() {
        when:
        def result = service.requestDataDump()

        then:
        result != null
        result instanceof DumpAcceptedDto
    }

}
