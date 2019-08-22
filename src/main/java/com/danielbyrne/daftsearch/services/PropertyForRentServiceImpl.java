package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.mappers.PropertyForRentMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;

import java.util.Set;
import java.util.stream.Collectors;

public class PropertyForRentServiceImpl implements PropertyForRentService {

    PropertyForRentMapper propertyForRentMapper;
    PropertyForRentRepository propertyForRentRepository;

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
}