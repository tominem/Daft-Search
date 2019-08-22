package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;

import java.util.Set;

public interface PropertyForSaleService {

    Set<PropertyForSaleDTO> getAllProperties();
}
