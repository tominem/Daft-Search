package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.LettingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;

import java.util.Set;

public interface PropertyForRentService {

    Set<PropertyForRentDTO> getAllProperties();

    Set<PropertyForRentDTO> filterPropertiesByDaftAttributes(LettingForm lettingForm);
    Set<PropertyForRentDTO> filterPropertiesByGoogle(Set<PropertyForRentDTO> preFilteredProps, LettingForm lettingForm);
}
