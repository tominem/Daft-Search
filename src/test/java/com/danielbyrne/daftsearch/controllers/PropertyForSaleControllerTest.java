package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PropertyForSaleControllerTest {

    @Mock
    PropertyForSaleService propertyForSaleService;

    PropertyForSaleController propertyForSaleController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        propertyForSaleController = new PropertyForSaleController(propertyForSaleService);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForSaleController).build();
    }

    @Test
    public void showForm() throws Exception {
        mockMvc.perform(get(PropertyForSaleController.BASE_URL + "/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales/searchform"));
    }

    @Test
    public void processSearchForm() throws Exception {

        Set<PropertyForSaleDTO> dtoSet = new HashSet<>(Arrays.asList(new PropertyForSaleDTO(), new PropertyForSaleDTO()));
        when(propertyForSaleService.filterPropertiesByDaftAttributes(any())).thenReturn(dtoSet);
        when(propertyForSaleService.filterPropertiesByGoogle(any(), any())).thenReturn(dtoSet);

        mockMvc.perform(get(PropertyForSaleController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("maxPrice", "1")
                .param("minBeds", "1")
                .param("distanceInKms", "50")
                .param("commuteInMinutes", "50")
                .param("modeOfTransport", "DRIVING")
                .param("counties", "dublin")
                .param("location", "my address"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales"))
                .andExpect(model().attributeExists("propertiesforsale"));

        verify(propertyForSaleService, times(1)).filterPropertiesByDaftAttributes(any());
        verify(propertyForSaleService, times(1)).filterPropertiesByGoogle(any(), any());
    }

    @Test
    public void processSearchFormWithBindingErrors() throws Exception {

        mockMvc.perform(get(PropertyForSaleController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales/searchform"))
                .andExpect(model().attributeExists("saleForm"));
    }


    @Test
    public void getAllProperties() throws Exception {

        Set<PropertyForSaleDTO> propertyForSaleDTOS = new HashSet<>(Arrays.asList(
                                                        new PropertyForSaleDTO(), new PropertyForSaleDTO()));

        given(propertyForSaleService.getAllProperties()).willReturn(propertyForSaleDTOS);

        mockMvc.perform(get(PropertyForSaleController.BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales"))
                .andExpect(model().attributeExists("propertiesforsale"));
    }
}