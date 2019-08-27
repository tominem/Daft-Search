package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForSaleDTO;
import com.danielbyrne.daftsearch.services.PropertyForSaleService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
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
    public void showForm() throws Exception {
        mockMvc.perform(get(PropertyForSaleController.BASE_URL + "/find"))
                .andExpect(status().isOk())
                .andExpect(view().name(PropertyForSaleController.BASE_URL + "/searchform"));
    }

    @Test
    public void processSearchForm() throws Exception {

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
                .andExpect(view().name("property/noresults"))
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