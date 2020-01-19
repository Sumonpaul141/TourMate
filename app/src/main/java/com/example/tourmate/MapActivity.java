package com.example.tourmate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

public class MapActivity extends AppCompatActivity {
    private LinearLayout atmLL, bankLL, airportLL, busStandLL, cafeLL, hospitalLL, hotelLL, mosqueLL, pharmacyLL, policeStationLL, restaurentLL, trainStation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setTitle("Searching for?");
        atmLL = findViewById(R.id.atmLL);
        bankLL = findViewById(R.id.bankLL);
        airportLL= findViewById(R.id.airportLL);
        busStandLL = findViewById(R.id.busStandLL);
        cafeLL =findViewById(R.id.cafeLL);
        hospitalLL = findViewById(R.id.hospitalLL);
        hotelLL = findViewById(R.id.hotelLL);
        mosqueLL = findViewById(R.id.mosqueLL);
        pharmacyLL = findViewById(R.id.pharmacyLL);
        policeStationLL = findViewById(R.id.bankLL);
        restaurentLL = findViewById(R.id.restaurentLL);
        trainStation = findViewById(R.id.trainStationLL);

        trainStation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "railway");
                startActivity(intent);
            }
        });
        policeStationLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "police");
                startActivity(intent);
            }
        });
        pharmacyLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "pharmacy");
                startActivity(intent);
            }
        });
        mosqueLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "mosque");
                startActivity(intent);
            }
        });
        hotelLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "hotel");
                startActivity(intent);
            }
        });
        cafeLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "cafe");
                startActivity(intent);
            }
        });
        busStandLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "busstand");
                startActivity(intent);
            }
        });
        airportLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "airport");
                startActivity(intent);
            }
        });
        atmLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "atm");
                startActivity(intent);
            }
        });
        restaurentLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "restaurant");
                startActivity(intent);
            }
        });
        hospitalLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "hospital");
                startActivity(intent);
            }
        });
        bankLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MapActivity.this, MapsMainActivity.class);
                intent.putExtra("type", "bank");
                startActivity(intent);
            }
        });

    }
}
