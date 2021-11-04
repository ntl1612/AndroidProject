package com.example.weatherwidget;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.weatherwidget.Common.Common;
import com.example.weatherwidget.Model.WeatherResult;
import com.example.weatherwidget.Retrofit.IOpenWeatherMap;
import com.example.weatherwidget.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class TodayWeatherFragment extends Fragment {

    ImageView img_weather;
    TextView cityName, temperature, wind, pressure, humidity,
            description, datetime, cloud, minTemperature, maxTemperature, feelsLike;
    LinearLayout weather_panel;
    ProgressBar loading;
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null) {
            instance = new TodayWeatherFragment();
        }
        return instance;
    }

    public TodayWeatherFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_today_weather, container, false);
        img_weather = itemView.findViewById(R.id.img_weather);
        cityName = itemView.findViewById(R.id.txt_city_name);
        temperature = itemView.findViewById(R.id.txt_temperature);
        minTemperature = itemView.findViewById(R.id.txt_minTemperature);
        maxTemperature = itemView.findViewById(R.id.txt_maxTemperature);
        feelsLike = itemView.findViewById(R.id.txt_feelsLike);
        wind = itemView.findViewById(R.id.txt_wind);
        pressure = itemView.findViewById(R.id.txt_pressure);
        humidity = itemView.findViewById(R.id.txt_humidity);
        description = itemView.findViewById(R.id.txt_description);
        datetime = itemView.findViewById(R.id.txt_date_time);
        cloud = itemView.findViewById(R.id.txt_cloud);

        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);

        getWeatherInformation();

        return itemView;
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLon(String.valueOf(Common.currentLocation.getLatitude()),
                String.valueOf(Common.currentLocation.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("http://openweathermap.org/img/wn/")
                                .append(weatherResult.getWeather().get(0).getIcon())
                                .append(".png").toString()).into(img_weather);
                        cityName.setText(weatherResult.getName());
                        description.setText(new StringBuilder("Weather in ")
                                .append(weatherResult.getName()).toString());
                        cloud.setText(new StringBuilder(weatherResult.getWeather().get(0).getMain())
                                .append(": ")
                        .append(weatherResult.getWeather().get(0).getDescription()));
                        temperature.setText(new StringBuilder(String.valueOf(weatherResult
                                .getMain().getTemp()))
                                .append("°C").toString());
                        minTemperature.setText(new StringBuilder("Min Temperature: ")
                                .append(weatherResult
                                .getMain().getTemp_min())
                                .append("°C").toString());
                        maxTemperature.setText(new StringBuilder("Max Temperature: ")
                                .append(weatherResult
                                .getMain().getTemp_max())
                                .append("°C").toString());
                        feelsLike.setText(new StringBuilder("Feels like: ")
                                .append(weatherResult
                                .getMain().getFeels_like())
                                .append("°C").toString());
                        datetime.setText(Common.convertUnixToDate(weatherResult.getDt()));
                        wind.setText(new StringBuilder("Speed: ")
                                .append(weatherResult.getWind().getSpeed())
                                .append("\nDeg: ")
                                .append(weatherResult.getWind().getDeg())
                                .append("\nGust: ")
                                .append(weatherResult.getWind().getGust())
                                .toString());
                        pressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure()))
                        .append(" hpa").toString());
                        humidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity()))
                                .append("%").toString());

                        weather_panel.setVisibility(View.VISIBLE);
                        loading.setVisibility(View.GONE);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), "" + throwable.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
        );
    }
}