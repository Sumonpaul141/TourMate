package com.example.tourmate;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tourmate.weatherAppActivity.Common.Common;
import com.example.tourmate.weatherAppActivity.Model.MyList;
import com.example.tourmate.weatherAppActivity.Model.WeatherForecastResult;
import com.squareup.picasso.Picasso;

public class WeatherForecastAdapter extends RecyclerView.Adapter<WeatherForecastAdapter.ViewHolder> {
    Context context;
    WeatherForecastResult weatherForecastResult;

    public WeatherForecastAdapter(Context context, WeatherForecastResult weatherForecastResult) {
        this.context = context;
        this.weatherForecastResult = weatherForecastResult;
    }

    @NonNull
    @Override
    public WeatherForecastAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.model_weather_forecast, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WeatherForecastAdapter.ViewHolder holder, int position) {

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherForecastResult.list.get(position).weather.get(0).getIcon()).append(".png").toString()).into(holder.imageWeather);
        holder.txtDate.setText(new StringBuilder(Common.convertUnixTiDate(weatherForecastResult.list.get(position).dt)));
        holder.txtDescription.setText(new StringBuilder(weatherForecastResult.list.get(position).weather.get(0).getDescription()));
        holder.txtTemparature.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getTemp())).append("°C").toString());
        holder.txtHumidity.setText(new StringBuilder(String.valueOf(weatherForecastResult.list.get(position).main.getHumidity())).append(" %").toString());


    }

    @Override
    public int getItemCount() {
        return weatherForecastResult.list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
         TextView txtDate, txtDescription, txtTemparature, txtHumidity;
         ImageView imageWeather;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageWeather = itemView.findViewById(R.id.mimageWeather);
            txtDate = itemView.findViewById(R.id.mtxtDate);
            txtDescription = itemView.findViewById(R.id.mtxtDescription);
            txtTemparature = itemView.findViewById(R.id.mtxtTemparature);
            txtHumidity = itemView.findViewById(R.id.mtxtHumidity);
        }
    }
}
