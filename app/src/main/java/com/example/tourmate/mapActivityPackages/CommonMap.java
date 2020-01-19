package com.example.tourmate.mapActivityPackages;

import com.example.tourmate.mapActivityPackages.retrofit.GoogleAPIServices;
import com.example.tourmate.mapActivityPackages.retrofit.RetrofitCLient;

public class CommonMap {
    private static final String GOOGLE_API_URL = "https://maps.googleapis.com/";
    public static GoogleAPIServices getGoogleAPIServices(){
        return RetrofitCLient.getClient(GOOGLE_API_URL).create(GoogleAPIServices.class);
    }
}
