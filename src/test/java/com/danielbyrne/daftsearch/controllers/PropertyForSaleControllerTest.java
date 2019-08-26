package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.County;
import com.danielbyrne.daftsearch.domain.ModeOfTransport;
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
    public void showForm() throws Exception {
        mockMvc.perform(get(PropertyForSaleController.BASE_URL + "/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales/searchform"));
    }

    @Test
    public void processSearchForm() throws Exception {

        // todo separate with and without error tests.

        String filterPropsUrlNoErrors = "/?maxPrice=200000&minBeds=2&distanceInKms=60&location=38+rathgar+road%2" +
                "C+dublin+6&commuteInMinutes=60&modeOfTransport=DRIVING&counties=dublin";

        String filterPropsUrlWithErrors = "/?maxPrice=200000&minBeds=2&distanceInKms=60&location=38+rathgar+road%2" +
                "C+dublin+6&commuteInMinutes=60&modeOfTransport=DRIVING";

        County[] counties = County.values();

        Set<PropertyForSaleDTO> p1 = new HashSet<>(Arrays.asList(new PropertyForSaleDTO(),
                new PropertyForSaleDTO()));

        Set<PropertyForSaleDTO> p2 = new HashSet<>(Arrays.asList(new PropertyForSaleDTO(),
                new PropertyForSaleDTO()));

        given(propertyForSaleService.filterPropertiesByDaftAttributes(200, 1, counties)).willReturn(p1);
        given(propertyForSaleService.filterPropertiesByGoogle(p1, "", ModeOfTransport.DRIVING, 1, 1)).willReturn(p2);

        mockMvc.perform(get(PropertyForSaleController.BASE_URL + filterPropsUrlNoErrors))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales"))
                .andExpect(model().attributeExists("propertiesforsale"));

        mockMvc.perform(get(PropertyForSaleController.BASE_URL + filterPropsUrlWithErrors))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales/searchform"));
    }

    @Test
    public void getAllProperties() throws Exception {

        Set<PropertyForSaleDTO> propertyForSaleDTOS = new HashSet<>(Arrays.asList(
                                                        new PropertyForSaleDTO(), new PropertyForSaleDTO()));

        given(propertyForSaleService.getAllProperties()).willReturn(propertyForSaleDTOS);

        mockMvc.perform(get(propertyForSaleController.BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sales"))
                .andExpect(model().attributeExists("propertiesforsale"));
    }
}