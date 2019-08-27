package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForRentDTO;
import com.danielbyrne.daftsearch.services.PropertyForRentService;
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

public class PropertyForRentControllerTest {

    @Mock
    PropertyForRentService propertyForRentService;
    PropertyForRentController propertyForRentController;

    MockMvc mockMvc;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        propertyForRentController = new PropertyForRentController(propertyForRentService);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForRentController).build();
    }

    @Test
    public void showForm() throws Exception {
        mockMvc.perform(get(PropertyForRentController.BASE_URL + "/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/lettings/searchform"));
    }


    @Test
    public void processSearchForm() throws Exception {

        Set<PropertyForRentDTO> dtoSet = new HashSet<>(Arrays.asList(new PropertyForRentDTO(), new PropertyForRentDTO()));
        when(propertyForRentService.filterPropertiesByDaftAttributes(any())).thenReturn(dtoSet);
        when(propertyForRentService.filterPropertiesByGoogle(any(), any())).thenReturn(dtoSet);

        mockMvc.perform(get(PropertyForRentController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("maxPrice", "1")
                .param("minBeds", "1")
                .param("distanceInKms", "50")
                .param("commuteInMinutes", "50")
                .param("modeOfTransport", "DRIVING")
                .param("counties", "dublin")
                .param("location", "my address"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/lettings"))
                .andExpect(model().attributeExists("propertiesForRent"));

        verify(propertyForRentService, times(1)).filterPropertiesByDaftAttributes(any());
        verify(propertyForRentService, times(1)).filterPropertiesByGoogle(any(), any());
    }

    @Test
    public void processSearchFormWithBindingErrors() throws Exception {
        mockMvc.perform(get(PropertyForRentController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("property/lettings/searchform"))
                .andExpect(model().attributeExists("lettingForm"));
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