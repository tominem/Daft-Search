package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;

import java.util.Set;

public interface PropertyForSharingService {

    Set<PropertyForSharingDTO> getAllProperties();
}
