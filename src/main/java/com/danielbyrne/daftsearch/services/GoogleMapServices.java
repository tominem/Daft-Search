package com.danielbyrne.daftsearch.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;

import java.io.IOException;

public class GoogleMapServices {

    private static final String API_KEY = "AIzaSyCYlpEWUItcCXYUuzdLHlaYM5xvjnkN5_E";

    public static DistanceMatrix getDrivingDistance(String origin, String destination)
            throws InterruptedException, ApiException, IOException {

        GeoApiContext distCalc = new GeoApiContext.Builder()
                                                .apiKey(API_KEY)
                                                .build();

        DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(distCalc);
        DistanceMatrix result = request.origins(origin)
                .destinations(destination)
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();

        return result;
    }
}