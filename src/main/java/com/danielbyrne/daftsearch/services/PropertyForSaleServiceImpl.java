package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSaleMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DistanceMatrix;
import com.google.maps.model.DistanceMatrixElement;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
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
    public Set<PropertyForSaleDTO> filterPropertiesByAttributes(int maxPrice, int minBed, County[] counties) {
        return propertyForSaleRepository.findAll()
                .stream()
                .filter(p -> p.getPrice() <= maxPrice
                        && p.getBeds() >= minBed
                        && Arrays.asList(counties).contains(p.getCounty()))
                .map(p -> {
                    PropertyForSaleDTO dto = propertyForSaleMapper.propertyForSaleToPropertyForSaleDTO(p);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForSaleDTO> filterPropertiesViaGoogle(Set<PropertyForSaleDTO> preFilteredDTOs, String origin,
                                                             ModeOfTransport modeOfTransport, int distance, int duration)
            throws InterruptedException, ApiException, IOException {

        Set<PropertyForSaleDTO> result = new HashSet<>();

        DistanceMatrix distanceMatrix;
        DistanceMatrixElement matrixElement;
        for (PropertyForSaleDTO dto : preFilteredDTOs) {

            distanceMatrix = googleMapServices.getDistanceMatrix(origin, dto.getAddress(), modeOfTransport);
            matrixElement = distanceMatrix.rows[0].elements[0];

            if (matrixElement != null) {
                dto.setReadableDistance(matrixElement.distance == null ? "" : matrixElement.distance.humanReadable);
                dto.setReadableDuration(matrixElement.duration == null ? "" : matrixElement.duration.humanReadable);
                dto.setDistanceKm(matrixElement.distance == null ? -1 : matrixElement.distance.inMeters/1000);
                dto.setDuranceMin(matrixElement.duration == null ? -1 : matrixElement.duration.inSeconds/60);

                if ((dto.getDistanceKm() >= 0 && dto.getDistanceKm() <= distance)
                    || (dto.getDuranceMin() >= 0 && dto.getDuranceMin() <= duration)) {
                    result.add(dto);
                }
            }
        }
        return result;
    }
}