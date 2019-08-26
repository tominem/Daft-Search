package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSaleMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSaleRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PropertyForSaleServiceImplTest {

    PropertyForSaleService propertyForSaleService;

    @Mock
    PropertyForSaleRepository propertyForSaleRepository;

    @Mock
    GoogleMapServices googleMapServices;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyForSaleService = new PropertyForSaleServiceImpl(PropertyForSaleMapper.INSTANCE,
                propertyForSaleRepository,
                googleMapServices);
    }

    @Test
    public void getAllProperties() {

        PropertyForSale p1 = new PropertyForSale();
        p1.setBaths(1);

        PropertyForSale p2 = new PropertyForSale();
        p2.setBeds(2);

        //given
        List<PropertyForSale> properties = Arrays.asList(p1, p2);

        when(propertyForSaleRepository.findAll()).thenReturn(properties);

        //when
        Set<PropertyForSaleDTO> propertyForSaleDTOS = propertyForSaleService.getAllProperties();

        //then
        assertEquals(2, propertyForSaleDTOS.size());
    }

    @Test
    public void filterPropertiesByDaftAttributes() {
        //todo
    }

    @Test
    public void filterPropertiesByGoogle() {
        //todo
    }
}