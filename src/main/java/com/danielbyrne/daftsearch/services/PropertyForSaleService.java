package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.util.Set;

public interface PropertyForSaleService {

    Set<PropertyForSaleDTO> getAllProperties();

    Set<PropertyForSaleDTO> filterPropertiesByAttributes(int maxPrice, int minBed, County[] counties);

    Set<PropertyForSaleDTO> filterPropertiesViaGoogle(Set<PropertyForSaleDTO> preFilteredDTOs, String origin,
                                                      ModeOfTransport modeOfTransport, int distance, int duration)
            throws InterruptedException, ApiException, IOException;
}
