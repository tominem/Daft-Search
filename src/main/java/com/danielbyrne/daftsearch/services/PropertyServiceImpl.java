package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.mappers.PropertyMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyDTO;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyMapper propertyMapper, PropertyRepository propertyRepository) {
        this.propertyMapper = propertyMapper;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public List<PropertyDTO> getAllProperties() {
        return propertyRepository.findAll()
                .stream()
                .map(propertyMapper::propertyToPropertyDTO)
                .collect(Collectors.toList());
    }
}