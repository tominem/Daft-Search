package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;

import java.util.Set;

public interface PropertyForRentService {

    Set<PropertyForRentDTO> getAllProperties();
}
