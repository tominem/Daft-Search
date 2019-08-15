package com.danielbyrne.daftsearch.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleMapServices {

    private static final String API_KEY = "AIzaSyCYlpEWUItcCXYUuzdLHlaYM5xvjnkN5_E";

    private static long getDrivingDistance() throws InterruptedException, ApiException, IOException {
        GeoApiContext distCalc = new GeoApiContext.Builder()
                                                .apiKey(API_KEY)
                                                .build();

        DistanceMatrixApiRequest request = DistanceMatrixApi.newRequest(distCalc);
        DistanceMatrix result = request.origins("38 Rathgar Road, Dublin 6")
                .destinations("Sandyford Business Centre")
                .mode(TravelMode.DRIVING)
                .language("en-US")
                .await();

        return result.rows[0].elements[0].distance.inMeters / 1000;
    }

    public static void main(String[] args) throws InterruptedException, ApiException, IOException {
        System.out.println(GoogleMapServices.getDrivingDistance());
    }
}