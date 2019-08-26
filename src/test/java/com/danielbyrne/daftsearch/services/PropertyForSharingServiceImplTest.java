package com.danielbyrne.daftsearch.services;

import com.danielbyrne.daftsearch.domain.PropertyForSharing;
import com.danielbyrne.daftsearch.domain.mappers.PropertyForSharingMapper;
import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.repositories.PropertyForSharingRepository;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

public class PropertyForSharingServiceImplTest {

    PropertyForSharingService propertyForSharingService;

    @Mock
    PropertyForSharingRepository propertyForSharingRepository;

    @Mock
    GoogleMapServices googleMapServices;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyForSharingService = new PropertyForSharingServiceImpl(PropertyForSharingMapper.INSTANCE,
                propertyForSharingRepository,
                googleMapServices);
    }

    @Test
    public void getAllProperties() {

        PropertyForSharing p1 = new PropertyForSharing();
        p1.setBaths(2);

        PropertyForSharing p2 = new PropertyForSharing();
        p2.setCurrentOccupants(1);

        //given
        List<PropertyForSharing> properties = Arrays.asList(p1, p2);

        when(propertyForSharingRepository.findAll()).thenReturn(properties);

        //when
        Set<PropertyForSharingDTO> propertyForSharingDTOS = propertyForSharingService.getAllProperties();

        assertEquals(2, propertyForSharingDTOS.size());
    }
}