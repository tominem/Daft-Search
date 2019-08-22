package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.mappers.PropertyMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyDTO;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class PropertyServiceImpl implements PropertyService {

    private final PropertyMapper propertyMapper;
    private final PropertyRepository propertyRepository;

    public PropertyServiceImpl(PropertyMapper propertyMapper, PropertyRepository propertyRepository) {
        this.propertyMapper = propertyMapper;
        this.propertyRepository = propertyRepository;
    }

    @Override
    public Set<PropertyDTO> getAllProperties() {

        Set<PropertyDTO> propertyDTOS = new HashSet<>();
        propertyRepository.findAll().iterator().forEachRemaining(property -> {
            PropertyDTO dto = propertyMapper.propertyToPropertyDTO(property);
            propertyDTOS.add(dto);
        });
        return propertyDTOS;
    }
}