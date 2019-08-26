package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.SharingForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.util.Set;

public interface PropertyForSharingService {

    Set<PropertyForSharingDTO> getAllProperties();

    Set<PropertyForSharingDTO> filterPropertiesByDaftAttributes(SharingForm sharingForm);

    Set<PropertyForSharingDTO> filterPropertiesByGoogle(Set<PropertyForSharingDTO> preFilteredProps, SharingForm sharingForm)
            throws InterruptedException, ApiException, IOException;
}
