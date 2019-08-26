package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForRentMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PropertyForRentServiceImpl implements PropertyForRentService {

    private final PropertyForRentMapper propertyForRentMapper;
    private final PropertyForRentRepository propertyForRentRepository;

    public PropertyForRentServiceImpl(PropertyForRentMapper propertyForRentMapper,
                                      PropertyForRentRepository propertyForRentRepository) {
        this.propertyForRentMapper = propertyForRentMapper;
        this.propertyForRentRepository = propertyForRentRepository;
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
                .filter(p -> p.getMonthlyRent() <= lettingForm.getMaxPrice()
                        && p.getBeds() >= lettingForm.getMinBeds()
                        && Arrays.asList(lettingForm.getCounties()).contains(p.getCounty()))
                .map(p -> {
                    PropertyForRentDTO dto = propertyForRentMapper.propertyForRentToPropertyForRentDTO(p);
                    return dto;
                })
                .collect(Collectors.toSet());
    }

    @Override
    public Set<PropertyForRentDTO> filterPropertiesByGoogle(Set<PropertyForRentDTO> preFilteredProps, LettingForm lettingForm) {
        return null;
    }
}