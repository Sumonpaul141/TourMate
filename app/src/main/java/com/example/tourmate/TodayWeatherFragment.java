package com.example.tourmate;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.weatherAppActivity.Common.Common;
import com.example.tourmate.weatherAppActivity.Model.WeatherForecastResult;
import com.example.tourmate.weatherAppActivity.Model.WeatherResult;
import com.example.tourmate.weatherAppActivity.Retrofit.OpenWeatherMap;
import com.example.tourmate.weatherAppActivity.Retrofit.RetrofitClient;
import com.squareup.picasso.Picasso;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class TodayWeatherFragment extends Fragment {

    private ImageView imageWeather;
    private TextView txtCityName, txtHumidity, txtSunrise, txtSunset, txtPressure, txtTemparature, txtDescription, txtDateTime, txtGeoCoord;
    private LinearLayout weatherLL;
    private ProgressBar progressBarWeather;
    private RecyclerView forecastRecyclerView;
    private WeatherForecastAdapter forecastAdapter;

    private CompositeDisposable compositeDisposable;
    OpenWeatherMap mService;

    static TodayWeatherFragment instance;

    public static TodayWeatherFragment getInstance() {
        if (instance == null){
            instance = new TodayWeatherFragment();
        }

        return instance;

    }

    public TodayWeatherFragment() {
        // Required empty public constructor
        compositeDisposable = new CompositeDisposable();
        Retrofit retrofit = RetrofitClient.getInstance();
        mService = retrofit.create(OpenWeatherMap.class);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_today_weather, container, false);
        init(view);
        getWeatherInformation();
        forecastRecyclerView = view.findViewById(R.id.forecastRecyclerView);
        forecastRecyclerView.setHasFixedSize(true);
        forecastRecyclerView.setLayoutManager(new LinearLayoutManager( getContext()));
        getForecastWeatherInfo();

        return view;
    }

    private void getForecastWeatherInfo() {

        compositeDisposable.add(mService.getForecastWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherForecastResult>() {
                    @Override
                    public void accept(WeatherForecastResult weatherForecastResult) throws Exception {
                        displayWeatherForecast(weatherForecastResult);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private void displayWeatherForecast(WeatherForecastResult weatherForecastResult) {
        forecastAdapter = new  WeatherForecastAdapter(getContext(), weatherForecastResult);
        forecastRecyclerView.setAdapter(forecastAdapter);
    }

    private void getWeatherInformation() {
        compositeDisposable.add(mService.getWeatherByLatLng(String.valueOf(Common.current_location.getLatitude()),
                String.valueOf(Common.current_location.getLongitude()),
                Common.APP_ID,
                "metric")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherResult>() {
                    @Override
                    public void accept(WeatherResult weatherResult) throws Exception {
                        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                        .append(weatherResult.getWeather().get(0).getIcon()).append(".png").toString()).into(imageWeather);


                        getActivity().setTitle(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                        txtCityName.setText(weatherResult.getName());
                        txtDescription.setText(new StringBuilder("Weather in ").append(weatherResult.getName()).toString());
                        txtTemparature.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getTemp())).append("°C").toString());
                        txtDateTime.setText(Common.convertUnixTiDate(weatherResult.getDt()));
                        txtPressure.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getPressure())).append(" hpa").toString());
                        txtHumidity.setText(new StringBuilder(String.valueOf(weatherResult.getMain().getHumidity())).append("%").toString());
//                        txtSunrise.setText(Common.convertUnixToHour(weatherResult.getSys().getSunrise()));
//                        txtSunset.setText(Common.convertUnixToHour(weatherResult.getSys().getSunset()));
//                        txtGeoCoord.setText(new StringBuilder("[").append(weatherResult.getCoord().toString()).append("]").toString());



                        weatherLL.setVisibility(View.VISIBLE);
                        progressBarWeather.setVisibility(View.GONE);

                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Toast.makeText(getActivity(), ""+throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
        );
    }

    private void init(View view) {
        imageWeather = view.findViewById(R.id.imageWeather);
        txtCityName = view.findViewById(R.id.txtCityNameTv);
        txtHumidity = view.findViewById(R.id.txtHumidity);
//        txtSunrise = view.findViewById(R.id.txtSunrise);
//        txtSunset = view.findViewById(R.id.txtSunset);
        txtPressure = view.findViewById(R.id.txtPressure);
        txtTemparature = view.findViewById(R.id.txtTemparature);
        txtDescription = view.findViewById(R.id.txtDescription);
        txtDateTime = view.findViewById(R.id.txtDateTime);
//        txtGeoCoord = view.findViewById(R.id.txtGeoCoord);
        weatherLL = view.findViewById(R.id.weatherLL);
        progressBarWeather = view.findViewById(R.id.progressBarWeather);

    }

}
