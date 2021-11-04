package com.example.weatherwidget;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.label305.asynctask.SimpleAsyncTask;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


public class CityFragment extends Fragment {


    private List<String> listCity;
    private MaterialSearchBar searchBar;
    ImageView img_weather;
    TextView cityName, temperature, wind, pressure, humidity, description, datetime, cloud;
    LinearLayout weather_panel;
    ProgressBar loading;
    CompositeDisposable compositeDisposable;
    IOpenWeatherMap mService;

    static CityFragment instance;

    public static CityFragment getInstance() {
        // Required empty public constructor
        if (instance == null) {
            instance = new CityFragment();
        }
        return instance;
    }

    public CityFragment() {
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(IOpenWeatherMap.class);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemView = inflater.inflate(R.layout.fragment_city, container, false);

        img_weather = itemView.findViewById(R.id.img_weather);
        cityName = itemView.findViewById(R.id.txt_city_name);
        temperature = itemView.findViewById(R.id.txt_temperature);
        wind = itemView.findViewById(R.id.txt_wind);
        pressure = itemView.findViewById(R.id.txt_pressure);
        humidity = itemView.findViewById(R.id.txt_humidity);
        description = itemView.findViewById(R.id.txt_description);
        datetime = itemView.findViewById(R.id.txt_date_time);
        cloud = itemView.findViewById(R.id.txt_cloud);

        weather_panel = itemView.findViewById(R.id.weather_panel);
        loading = itemView.findViewById(R.id.loading);

        searchBar = itemView.findViewById(R.id.searchBar);

        searchBar.setEnabled(false);
        weather_panel.setVisibility(View.INVISIBLE);

        new LoadCity().execute();
        return itemView;
    }

    private class LoadCity extends SimpleAsyncTask<List<String>> {

        @Override
        protected List<String> doInBackgroundSimple() {
            listCity = new ArrayList<>();
            try {
                StringBuilder builder = new StringBuilder();
                InputStream is = getResources().openRawResource(R.raw.city_list);
                GZIPInputStream gzipInputStream = new GZIPInputStream(is);
                InputStreamReader reader = new InputStreamReader(gzipInputStream);
                BufferedReader in = new BufferedReader(reader);

                String readed;
                while ((readed = in.readLine()) != null) {
                    builder.append(readed);
                    listCity = new Gson().fromJson(builder.toString(), new TypeToken<List<String>>() {
                    }.getType());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return listCity;
        }

        @Override
        protected void onSuccess(List<String> list) {
            super.onSuccess(list);

            searchBar.setEnabled(true);
            searchBar.addTextChangeListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    List<String> suggest = new ArrayList<>();
                    for (String search : list) {
                        if (search.toLowerCase().contains(searchBar.getText().toLowerCase())) {
                            suggest.add(search);
                        }
                    }
                }

                @Override
                public void afterTextChanged(Editable editable) {

                }
            });
            searchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
                @Override
                public void onSearchStateChanged(boolean enabled) {

                }

                @Override
                public void onSearchConfirmed(CharSequence text) {
                    getWeatherInformation(text.toString());
                    searchBar.setLastSuggestions(list);
                }

                @Override
                public void onButtonClicked(int buttonCode) {

                }
            });
            searchBar.setLastSuggestions(list);

            loading.setVisibility(View.GONE);
        }
    }

    private void getWeatherInformation(String city) {
        compositeDisposable.add(mService.getWeatherByCity(city,
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

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        compositeDisposable.clear();
        super.onStop();
    }
}
