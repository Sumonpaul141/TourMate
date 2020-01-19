package com.example.tourmate;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tourmate.mapActivityPackages.CommonMap;
import com.example.tourmate.mapActivityPackages.model.MyPlaces;
import com.example.tourmate.mapActivityPackages.model.Results;
import com.example.tourmate.mapActivityPackages.retrofit.GoogleAPIServices;
import com.example.tourmate.weatherAppActivity.Common.Common;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapsMainActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Location mLastLocation;
    private Marker mMarker;
    private double latitude, longititude;
    private MyPlaces currentPlace;
    private GoogleAPIServices mService;
    private TextView nearbyPlacesTv;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;
    private Button button;
    private String searchPlace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_main);
        nearbyPlacesTv = findViewById(R.id.nearbyPlacesTv);
        mService = CommonMap.getGoogleAPIServices();

        button = findViewById(R.id.button);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        if (getIntent().getStringExtra("type") == null){
            Toast.makeText(MapsMainActivity.this, "NO DAta", Toast.LENGTH_SHORT).show();
        }else {
            searchPlace = getIntent().getStringExtra("type");
            nearbyPlacesTv.setText(searchPlace);
            button.setText(new StringBuilder("Click here for all ").append(searchPlace).toString());
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nearByPlaces(searchPlace);
            }
        });


    }

    private void nearByPlaces(String placeType) {
        mMap.clear();
        String url = getUrl(latitude, longititude, placeType);
        mService.getNearbyPlaces(url).enqueue(new Callback<MyPlaces>() {
            @Override
            public void onResponse(Call<MyPlaces> call, Response<MyPlaces> response) {
                if (response.isSuccessful()){
                    for (int i=0; i<response.body().getResults().length; i++ ){
                        MarkerOptions markerOptions = new MarkerOptions();
                        Results googlePlace = response.body().getResults()[i];
                        double lat = Double.parseDouble(googlePlace.getGeometry().getLocation().getLat());
                        double lng = Double.parseDouble(googlePlace.getGeometry().getLocation().getLng());
                        String placeName = googlePlace.getName();
                        String vacinity = googlePlace.getVicinity();
                        LatLng latLng = new LatLng(lat, lng);
                        markerOptions.position(latLng);
                        markerOptions.title(placeName);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                        mMap.addMarker(markerOptions);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));

                    }
                }
            }

            @Override
            public void onFailure(Call<MyPlaces> call, Throwable t) {
                Toast.makeText(MapsMainActivity.this, ""+t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String getUrl(double latitude, double longititude, String placeType) {
        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        googlePlacesUrl.append("location="+latitude+","+longititude);
        googlePlacesUrl.append("&radius="+ 10000);
        googlePlacesUrl.append("&type="+placeType);
        googlePlacesUrl.append("&sensor=ture");
        googlePlacesUrl.append("&key="+getResources().getString(R.string.browser_key));
        Log.d("getUrl", googlePlacesUrl.toString());
        return googlePlacesUrl.toString();
    }

    @Override
    protected void onStop() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
        super.onStop();

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Dexter.withActivity(this).withPermissions(Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if (report.areAllPermissionsGranted()){
                            if (ActivityCompat.checkSelfPermission(MapsMainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(MapsMainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION ) != PackageManager.PERMISSION_GRANTED) {
                                return;
                            }
                            buildLocationRequest();
                            buildLocationCallBack();
                            nearbyPlacesTv.setText(searchPlace);
                            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(MapsMainActivity.this);
                            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        Toast.makeText(MapsMainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }).check();



    }

    private void buildLocationCallBack() {
        locationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mLastLocation = locationResult.getLastLocation();
                if (mMarker != null){
                    mMarker.remove();
                }
                latitude = mLastLocation.getLatitude();
                longititude = mLastLocation.getLongitude();

                LatLng latLng = new LatLng(latitude, longititude);
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("Your Current Location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET)));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));



            }
        };
    }

    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setFastestInterval(7000);
        locationRequest.setFastestInterval(3500);
        locationRequest.setSmallestDisplacement(10.0f);

    }


    public void onBackArrow(View view) {
        startActivity(new Intent(MapsMainActivity.this, MapActivity.class));
    }
}
