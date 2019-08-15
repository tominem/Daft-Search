package com.danielbyrne.daftsearch.services;

import com.google.maps.DistanceMatrixApi;
import com.google.maps.DistanceMatrixApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.TravelMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.io.IOException;

@Configuration
@PropertySource("classpath:api.properties")
public class GoogleMapServices {

    @Value("${api.key}")
    private String API_KEY;

    public DistanceMatrix getDrivingDistance(String origin, String destination)
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