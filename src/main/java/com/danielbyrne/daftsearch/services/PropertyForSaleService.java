package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;

import java.util.Set;

public interface PropertyForSaleService {

    Set<PropertyForSaleDTO> getAllProperties();

    Set<PropertyForSaleDTO> filterProperties(int maxPrice, int minBed, County[] counties);
}
