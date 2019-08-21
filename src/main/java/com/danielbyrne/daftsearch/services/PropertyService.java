package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.model.PropertyDTO;

import java.util.Set;

public interface PropertyService {

    Set<PropertyDTO> getAllProperties();
}
