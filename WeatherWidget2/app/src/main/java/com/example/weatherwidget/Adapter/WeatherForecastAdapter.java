package com.example.weatherwidget.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.weatherwidget.Common.Common;
import com.example.weatherwidget.Model.WeatherForecast;
import com.example.weatherwidget.R;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder> {

    Context context;
    WeatherForecast weatherForecast;

    public WeatherForecastAdapter(Context context, WeatherForecast weatherForecast) {
        this.context = context;
        this.weatherForecast = weatherForecast;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_weather_forecast,parent,false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        Picasso.get().load(new StringBuilder("http://openweathermap.org/img/wn/")
                .append(weatherForecast.list.get(position).weather.get(0).getIcon())
                .append(".png").toString()).into(holder.img_weather);

        holder.datetime.setText(new StringBuilder(Common.convertUnixToDate
                (weatherForecast.list.get(position).dt)));
        holder.description.setText(new StringBuilder(
                weatherForecast.list.get(position).weather.get(0).getDescription()));
        holder.temperature.setText(new StringBuilder(String.valueOf(
                weatherForecast.list.get(position).main.getTemp())).append("°C"));
    }

    @Override
    public int getItemCount() {
        return weatherForecast.list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView datetime,description,temperature, cloud;
        ImageView img_weather;
        public MyViewHolder(View itemView) {
            super(itemView);
            img_weather = itemView.findViewById(R.id.img_weather);
            datetime = itemView.findViewById(R.id.txt_date);
            description = itemView.findViewById(R.id.txt_description);
            temperature = itemView.findViewById(R.id.txt_temperature);
            cloud = itemView.findViewById(R.id.txt_cloud);
        }
    }
}
