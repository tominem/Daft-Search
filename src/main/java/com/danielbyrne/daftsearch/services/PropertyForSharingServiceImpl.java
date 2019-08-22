package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.mappers.PropertyForSharingMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PropertyForSharingServiceImpl implements PropertyForSharingService {

    PropertyForSharingMapper propertyForSharingMapper;
    PropertyForSharingRepository propertyForSharingRepository;

    public PropertyForSharingServiceImpl(PropertyForSharingMapper propertyForSharingMapper,
                                         PropertyForSharingRepository propertyForSharingRepository) {
        this.propertyForSharingMapper = propertyForSharingMapper;
        this.propertyForSharingRepository = propertyForSharingRepository;
    }

    @Override
    public Set<PropertyForSharingDTO> getAllProperties() {
        return propertyForSharingRepository.findAll()
                .stream()
                .map(propertyForSharingMapper::propertyForSharingToPropertyForSharingDTO)
                .collect(Collectors.toSet());
    }
}
