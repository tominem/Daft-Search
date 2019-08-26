package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSharingMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
import com.google.maps.errors.ApiException;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class PropertyForSharingServiceImpl implements PropertyForSharingService {

    private final PropertyForSharingMapper propertyForSharingMapper;
    private final PropertyForSharingRepository propertyForSharingRepository;

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
        return null;
    }
}