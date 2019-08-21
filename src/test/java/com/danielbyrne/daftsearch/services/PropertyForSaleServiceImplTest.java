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
import java.util.List;

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

        //given
        List<Property> properties = Arrays.asList(new PropertyForSale(), new PropertyForSale(), new PropertyForSale());

        when(propertyRepository.findAll()).thenReturn(properties);

        //when
        List<PropertyDTO> propertyDTOS = propertyService.getAllProperties();

        //then
        assertEquals(3, propertyDTOS.size());
    }
}