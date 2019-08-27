package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSharingMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyForSharingServiceImpl implements PropertyForSharingService {

    private final PropertyForSharingMapper propertyForSharingMapper;
    private final PropertyForSharingRepository propertyForSharingRepository;
    private final GoogleMapServices googleMapServices;

    public PropertyForSharingServiceImpl(PropertyForSharingMapper propertyForSharingMapper,
                                         PropertyForSharingRepository propertyForSharingRepository,
                                         GoogleMapServices googleMapServices) {
        this.propertyForSharingMapper = propertyForSharingMapper;
        this.propertyForSharingRepository = propertyForSharingRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public Set<PropertyForSharingDTO> getAllProperties() {
        return propertyForSharingRepository.findAll()
                .stream()
                .map(propertyForSharingMapper::propertyForSharingToPropertyForSharingDTO)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForSharingDTO> filterPropertiesByDaftAttributes(SharingForm sharingForm) {
        return propertyForSharingRepository.findAll()
                .stream()
                .filter(p -> p.getMonthlyRent() <= sharingForm.getMaxPrice()
                        && Arrays.asList(sharingForm.getCounties()).contains(p.getCounty()))
                .filter(p -> // filter by room types
                        (p.isHasDouble() && sharingForm.isHasDouble())
                        || (p.isHasSingle() && sharingForm.isHasSingle())
                        || (p.isHasSharedRoom() && sharingForm.isHasShared())
                )
                .filter(p -> // filter by genders
                        (p.isMalesOnly() && sharingForm.isMale())
                        || (p.isFemalesOnly() && !sharingForm.isMale())
                        || (p.getMalesOrFemales().equals("Males or Females")))
                .map(p -> {
                    PropertyForSharingDTO dto = propertyForSharingMapper.propertyForSharingToPropertyForSharingDTO(p);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForSharingDTO> filterPropertiesByGoogle(Set<PropertyForSharingDTO> preFilteredProps, SharingForm sharingForm)
            throws InterruptedException, ApiException, IOException {

        List<PropertyForSharingDTO> propertiesToQuery = new ArrayList<>();
        Set<PropertyForSharingDTO> postFilteredDTOs = new HashSet<>();

        for (PropertyForSharingDTO dto : preFilteredProps){
            if (propertiesToQuery.size() < 100) {
                propertiesToQuery.add(dto);
            } else {
                postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, sharingForm));
                propertiesToQuery = new ArrayList<>();
            }
        }
        postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, sharingForm));

        return postFilteredDTOs;
    }

    private Set<PropertyForSharingDTO> callGoogleApi(List<PropertyForSharingDTO> properties, SharingForm sharingForm)
            throws InterruptedException, ApiException, IOException {

        Set<PropertyForSharingDTO> result = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for (PropertyForSharingDTO dto : properties) {
            sb.append(dto.getAddress() + " | ");
        }

        String destStr = sb.toString().substring(0, sb.toString().lastIndexOf("|")-1);

        DistanceMatrix distanceMatrix = googleMapServices.getDistanceMatrix(sharingForm.getLocation(),
                destStr,
                sharingForm.getModeOfTransport());

        int i, j;
        for (i = 0; i < distanceMatrix.rows.length; i++){
            for (j = 0; j < distanceMatrix.rows[i].elements.length; j++) {

                PropertyForSharingDTO tempProperty = properties.get(j);

                tempProperty.setReadableDuration(distanceMatrix.rows[i].elements[j].duration == null ? "" : distanceMatrix.rows[i].elements[j].duration.humanReadable);
                tempProperty.setReadableDistance(distanceMatrix.rows[i].elements[j].distance == null ? "" : distanceMatrix.rows[i].elements[j].distance.humanReadable);
                tempProperty.setDistanceKm(distanceMatrix.rows[i].elements[j].distance == null ? -1 : distanceMatrix.rows[i].elements[j].distance.inMeters/1000);
                tempProperty.setDurationMin(distanceMatrix.rows[i].elements[j].duration == null ? -1 : distanceMatrix.rows[i].elements[j].duration.inSeconds/60);

                System.out.println(distanceMatrix.destinationAddresses[j] + " | " + tempProperty.getAddress() + " | " + tempProperty.getDistanceKm() + " | " + tempProperty.getDurationMin() );

                if ((tempProperty.getDistanceKm() >= 0 && tempProperty.getDistanceKm() <= sharingForm.getDistanceInKms())
                        || (tempProperty.getDurationMin() >= 0 && tempProperty.getDurationMin() <= sharingForm.getCommuteInMinutes())) {
                    result.add(tempProperty);
                }
            }
        }
        return result;
    }
}