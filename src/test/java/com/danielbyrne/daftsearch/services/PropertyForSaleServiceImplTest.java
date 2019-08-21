package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.Property;
import com.danielbyrne.daftsearch.domain.PropertyForSale;
import com.danielbyrne.daftsearch.domain.mappers.PropertyMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyDTO;
import com.danielbyrne.daftsearch.repositories.PropertyRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PropertyForSaleServiceImplTest {

    PropertyService propertyService;

    @Mock
    PropertyRepository propertyRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyService = new PropertyServiceImpl(PropertyMapper.INSTANCE, propertyRepository);
    }

    @Test
    public void getAllProperties() {

        Property p1 = new PropertyForSale();
        p1.setBaths(1);

        Property p2 = new PropertyForSale();
        p2.setBeds(2);

        //given
        Set<Property> properties = new HashSet<>(Arrays.asList(p1, p2));

        when(propertyRepository.findAll()).thenReturn(properties);

        //when
        Set<PropertyDTO> propertyDTOS = propertyService.getAllProperties();

        //then
        assertEquals(2, propertyDTOS.size());
    }
}