package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PropertyForRentControllerTest {

    @Mock
    PropertyForRentService propertyForRentService;

    @InjectMocks
    PropertyForRentController propertyForRentController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForRentController).build();
    }

    @Test
    public void getAllProperties() throws Exception {

        Set<PropertyForRentDTO> propertyForRentDTOS = new HashSet<>(
                Arrays.asList(new PropertyForRentDTO(), new PropertyForRentDTO()));

        given(propertyForRentService.getAllProperties()).willReturn(propertyForRentDTOS);

        mockMvc.perform(get(PropertyForRentController.BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/lettings"))
                .andExpect(model().attributeExists("propertiesForRent"));
    }
}