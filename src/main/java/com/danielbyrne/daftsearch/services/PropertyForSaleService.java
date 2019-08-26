package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.forms.SaleForm;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.google.maps.errors.ApiException;

import java.io.IOException;
import java.util.Set;

public interface PropertyForSaleService {

    Set<PropertyForSaleDTO> getAllProperties();

    Set<PropertyForSaleDTO> filterPropertiesByDaftAttributes(SaleForm saleForm);

    Set<PropertyForSaleDTO> filterPropertiesByGoogle(Set<PropertyForSaleDTO> preFilteredDTOs, SaleForm saleForm)
            throws InterruptedException, ApiException, IOException;
}
