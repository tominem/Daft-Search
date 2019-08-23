package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
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

public class PropertyForSaleControllerTest {

    @Mock
    PropertyForSaleService propertyForSaleService;

    @InjectMocks
    PropertyForSaleController propertyForSaleController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForSaleController).build();
    }

    @Test
    public void getAllProperties() throws Exception {

        Set<PropertyForSaleDTO> propertyForSaleDTOS = new HashSet<>(Arrays.asList(
                                                        new PropertyForSaleDTO(), new PropertyForSaleDTO()));

        given(propertyForSaleService.getAllProperties()).willReturn(propertyForSaleDTOS);

        mockMvc.perform(get("/properties/sales/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales"))
                .andExpect(model().attributeExists("propertiesforsale"));
    }
}