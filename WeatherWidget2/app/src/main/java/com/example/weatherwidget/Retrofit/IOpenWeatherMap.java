package com.example.weatherwidget.Retrofit;

import com.example.weatherwidget.Model.WeatherForecast;
import com.example.weatherwidget.Model.WeatherResult;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IOpenWeatherMap {
    @GET("weather")
    Observable<WeatherResult> getWeatherByLatLon(@Query("lat") String lat,
                                                 @Query("lon") String lon,
                                                 @Query("appid") String appid,
                                                 @Query("units") String units);

    @GET("forecast")
    Observable<WeatherForecast> getForecast(@Query("lat") String lat,
                                            @Query("lon") String lon,
                                            @Query("appid") String appid,
                                            @Query("units") String units);

    @GET("weather")
    Observable<WeatherResult> getWeatherByCity(@Query("q") String cityName,
                                                 @Query("appid") String appid,
                                                 @Query("units") String units);
}
