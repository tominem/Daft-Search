package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.PropertyForRent;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForRentMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForRentRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PropertyForRentServiceImplTest {

    PropertyForRentService propertyForRentService;

    @Mock
    PropertyForRentRepository propertyForRentRepository;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyForRentService = new PropertyForRentServiceImpl(PropertyForRentMapper.INSTANCE,
                propertyForRentRepository);
    }

    @Test
    public void getAllProperties() {

        PropertyForRent p1 = new PropertyForRent();
        p1.setBaths(1);

        PropertyForRent p2 = new PropertyForRent();
        p2.setBeds(2);

        //given
        List<PropertyForRent> properties = Arrays.asList(p1, p2);

        when(propertyForRentRepository.findAll()).thenReturn(properties);

        //when
        Set<PropertyForRentDTO> propertyForSaleDTOS = propertyForRentService.getAllProperties();

        //then
        assertEquals(2, propertyForSaleDTOS.size());
    }
}