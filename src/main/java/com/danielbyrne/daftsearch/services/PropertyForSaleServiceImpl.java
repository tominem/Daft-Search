package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSaleMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PropertyForSaleServiceImpl implements PropertyForSaleService {

    private final PropertyForSaleMapper propertyForSaleMapper;
    private final PropertyForSaleRepository propertyForSaleRepository;

    public PropertyForSaleServiceImpl(PropertyForSaleMapper propertyForSaleMapper,
                                      PropertyForSaleRepository propertyForSaleRepository) {
        this.propertyForSaleMapper = propertyForSaleMapper;
        this.propertyForSaleRepository = propertyForSaleRepository;
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
    public Set<PropertyForSaleDTO> filterProperties(int maxPrice, int minBed, County[] counties) {
        return propertyForSaleRepository.findAll()
                .stream()
                .filter(p -> p.getPrice() <= maxPrice
                        && p.getBeds() >= minBed
                        && Arrays.asList(counties).contains(p.getCounty()))
                .map(propertyForSale -> {
                    PropertyForSaleDTO dto = propertyForSaleMapper.propertyForSaleToPropertyForSaleDTO(propertyForSale);
                    return dto;
                })
                .collect(Collectors.toSet());
    }
}