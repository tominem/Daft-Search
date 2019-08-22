package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.mappers.PropertyForSaleMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class PropertyForSaleServiceImpl implements PropertyForSaleService {

    private final PropertyForSaleMapper propertyForSaleMapper;
    private final PropertyForSaleRepository propertyForSaleRepository;

    public PropertyForSaleServiceImpl(PropertyForSaleMapper propertyForSaleMapper,
                                      PropertyForSaleRepository propertyForSaleRepository) {
        this.propertyForSaleMapper = propertyForSaleMapper;
        this.propertyForSaleRepository = propertyForSaleRepository;
    }

    @Override
    public Set<PropertyForSaleDTO> getAllProperties() {
        return null;
    }
}
