package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForRentMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyForRentServiceImpl implements PropertyForRentService {

    private final PropertyForRentMapper propertyForRentMapper;
    private final PropertyForRentRepository propertyForRentRepository;
    private final GoogleMapServices googleMapServices;

    public PropertyForRentServiceImpl(PropertyForRentMapper propertyForRentMapper,
                                      PropertyForRentRepository propertyForRentRepository,
                                      GoogleMapServices googleMapServices) {

        this.propertyForRentMapper = propertyForRentMapper;
        this.propertyForRentRepository = propertyForRentRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public Set<PropertyForRentDTO> getAllProperties() {
        return propertyForRentRepository.findAll()
                .stream()
                .map(propertyForRentMapper::propertyForRentToPropertyForRentDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForRentDTO> filterPropertiesByDaftAttributes(LettingForm lettingForm) {
        return propertyForRentRepository.findAll()
                .stream()
                .filter(p ->
                        p.getMonthlyRent() <= lettingForm.getMaxPrice()
                        && p.getBeds() >= lettingForm.getMinBeds()
                        && Arrays.asList(lettingForm.getCounties()).contains(p.getCounty()))
                .map(p -> {
                    PropertyForRentDTO dto = propertyForRentMapper.propertyForRentToPropertyForRentDTO(p);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForRentDTO> filterPropertiesByGoogle(Set<PropertyForRentDTO> preFilteredProps, LettingForm lettingForm)
            throws InterruptedException, ApiException, IOException {

        List<PropertyForRentDTO> propertiesToQuery = new ArrayList<>();
        Set<PropertyForRentDTO> postFilteredDTOs = new HashSet<>();

        for (PropertyForRentDTO dto : preFilteredProps){
            if (propertiesToQuery.size() < 100) {
                propertiesToQuery.add(dto);
            } else {
                postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, lettingForm));
                propertiesToQuery = new ArrayList<>();
            }
        }
        postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, lettingForm));

        return postFilteredDTOs;
    }

    private Set<PropertyForRentDTO> callGoogleApi(List<PropertyForRentDTO> properties, LettingForm lettingForm)
            throws InterruptedException, ApiException, IOException {

        Set<PropertyForRentDTO> result = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for (PropertyForRentDTO dto : properties) {
            sb.append(dto.getAddress() + " | ");
        }

        String destStr = sb.toString().substring(0, sb.toString().lastIndexOf("|")-1);

        DistanceMatrix distanceMatrix = googleMapServices.getDistanceMatrix(lettingForm.getLocation(),
                                                                            destStr,
                                                                            lettingForm.getModeOfTransport());

        int i, j;
        for (i = 0; i < distanceMatrix.rows.length; i++){
            for (j = 0; j < distanceMatrix.rows[i].elements.length; j++) {

                PropertyForRentDTO tempProperty = properties.get(j);

                tempProperty.setReadableDuration(distanceMatrix.rows[i].elements[j].duration == null ? "" : distanceMatrix.rows[i].elements[j].duration.humanReadable);
                tempProperty.setReadableDistance(distanceMatrix.rows[i].elements[j].distance == null ? "" : distanceMatrix.rows[i].elements[j].distance.humanReadable);
                tempProperty.setDistanceKm(distanceMatrix.rows[i].elements[j].distance == null ? -1 : distanceMatrix.rows[i].elements[j].distance.inMeters/1000);
                tempProperty.setDurationMin(distanceMatrix.rows[i].elements[j].duration == null ? -1 : distanceMatrix.rows[i].elements[j].duration.inSeconds/60);

                System.out.println(distanceMatrix.destinationAddresses[j] + " | " + tempProperty.getAddress() + " | " + tempProperty.getDistanceKm() + " | " + tempProperty.getDurationMin() );

                if ((tempProperty.getDistanceKm() >= 0 && tempProperty.getDistanceKm() <= lettingForm.getDistanceInKms())
                        || (tempProperty.getDurationMin() >= 0 && tempProperty.getDurationMin() <= lettingForm.getCommuteInMinutes())) {
                    result.add(tempProperty);
                }
            }
        }
        return result;
    }
}