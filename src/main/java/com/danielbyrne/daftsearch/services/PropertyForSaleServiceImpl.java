package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSaleMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class PropertyForSaleServiceImpl implements PropertyForSaleService {

    private final PropertyForSaleMapper propertyForSaleMapper;
    private final PropertyForSaleRepository propertyForSaleRepository;
    private final GoogleMapServices googleMapServices;

    public PropertyForSaleServiceImpl(PropertyForSaleMapper propertyForSaleMapper,
                                      PropertyForSaleRepository propertyForSaleRepository,
                                      GoogleMapServices googleMapServices) {
        this.propertyForSaleMapper = propertyForSaleMapper;
        this.propertyForSaleRepository = propertyForSaleRepository;
        this.googleMapServices = googleMapServices;
    }

    @Override
    public Set<PropertyForSaleDTO> getAllProperties() {
        return propertyForSaleRepository.findAll()
                .stream()
                .map(propertyForSale -> {
                    PropertyForSaleDTO dto = propertyForSaleMapper.propertyForSaleToPropertyForSaleDTO(propertyForSale);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForSaleDTO> filterPropertiesByDaftAttributes(SaleForm saleForm) {
        return propertyForSaleRepository.findAll()
                .stream()
                .filter(p -> p.getPrice() <= saleForm.getMaxPrice()
                        && p.getBeds() >= saleForm.getMinBeds()
                        && Arrays.asList(saleForm.getCounties()).contains(p.getCounty()))
                .map(p -> {
                    PropertyForSaleDTO dto = propertyForSaleMapper.propertyForSaleToPropertyForSaleDTO(p);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForSaleDTO> filterPropertiesByGoogle(Set<PropertyForSaleDTO> preFilteredDTOs, SaleForm saleForm)
            throws InterruptedException, ApiException, IOException {

        List<PropertyForSaleDTO> propertiesToQuery = new ArrayList<>();
        Set<PropertyForSaleDTO> postFilteredDTOs = new HashSet<>();

        for(PropertyForSaleDTO dto : preFilteredDTOs) {
            if (propertiesToQuery.size() < 100){
                propertiesToQuery.add(dto);
            } else {
                postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, saleForm));
                propertiesToQuery = new ArrayList<>();
            }
        }
        postFilteredDTOs.addAll(callGoogleApi(propertiesToQuery, saleForm));

        return postFilteredDTOs;
    }

    private Set<PropertyForSaleDTO> callGoogleApi(List<PropertyForSaleDTO> properties, SaleForm saleForm)
            throws InterruptedException, ApiException, IOException {

        Set<PropertyForSaleDTO> result = new HashSet<>();
        StringBuilder sb = new StringBuilder();

        for (PropertyForSaleDTO dto : properties) {
            sb.append(dto.getAddress() + " | ");
        }

        String destStr = sb.toString().substring(0, sb.toString().lastIndexOf("|")-1);

        DistanceMatrix distanceMatrix = googleMapServices.getDistanceMatrix(saleForm.getLocation(), destStr, saleForm.getModeOfTransport());

        int i, j;
        for (i = 0; i < distanceMatrix.rows.length; i++){
            for (j = 0; j < distanceMatrix.rows[i].elements.length; j++) {

                PropertyForSaleDTO tempProperty = properties.get(j);

                tempProperty.setReadableDuration(distanceMatrix.rows[i].elements[j].duration == null ? "" : distanceMatrix.rows[i].elements[j].duration.humanReadable);
                tempProperty.setReadableDistance(distanceMatrix.rows[i].elements[j].distance == null ? "" : distanceMatrix.rows[i].elements[j].distance.humanReadable);
                tempProperty.setDistanceKm(distanceMatrix.rows[i].elements[j].distance == null ? -1 : distanceMatrix.rows[i].elements[j].distance.inMeters/1000);
                tempProperty.setDurationMin(distanceMatrix.rows[i].elements[j].duration == null ? -1 : distanceMatrix.rows[i].elements[j].duration.inSeconds/60);

                if ((tempProperty.getDistanceKm() >= 0 && tempProperty.getDistanceKm() <= saleForm.getDistanceInKms())
                        || (tempProperty.getDurationMin() >= 0 && tempProperty.getDurationMin() <= saleForm.getCommuteInMinutes())) {
                    result.add(tempProperty);
                }
            }
        }
        return result;
    }
}