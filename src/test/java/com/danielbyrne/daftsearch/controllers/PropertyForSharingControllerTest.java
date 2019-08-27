package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.services.PropertyForSharingService;
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

public class PropertyForSharingControllerTest {

    @Mock
    PropertyForSharingService propertyForSharingService;
    PropertyForSharingController propertyForSharingController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        propertyForSharingController = new PropertyForSharingController(propertyForSharingService);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForSharingController).build();
    }

    @Test
    public void showForm() throws Exception {
        mockMvc.perform(get(PropertyForSharingController.BASE_URL + "/find"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sharing/searchform"));
    }


    @Test
    public void processSearchForm() throws Exception {

        Set<PropertyForSharingDTO> dtoSet = new HashSet<>(Arrays.asList(new PropertyForSharingDTO(), new PropertyForSharingDTO()));
        when(propertyForSharingService.filterPropertiesByDaftAttributes(any())).thenReturn(dtoSet);
        when(propertyForSharingService.filterPropertiesByGoogle(any(), any())).thenReturn(dtoSet);

        mockMvc.perform(get(PropertyForSharingController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("maxPrice", "1")
                .param("minBeds", "1")
                .param("distanceInKms", "50")
                .param("commuteInMinutes", "50")
                .param("modeOfTransport", "DRIVING")
                .param("counties", "dublin")
                .param("location", "my address")
                .param("roomType", "double")
                .param("male", "true"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sharing"))
                .andExpect(model().attributeExists("propertiesToShare"));

        verify(propertyForSharingService, times(1)).filterPropertiesByDaftAttributes(any());
        verify(propertyForSharingService, times(1)).filterPropertiesByGoogle(any(), any());
    }

    @Test
    public void processSearchFormWithBindingErrors() throws Exception {
        mockMvc.perform(get(PropertyForSharingController.BASE_URL)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sharing/searchform"))
                .andExpect(model().attributeExists("sharingForm"));
    }

    @Test
    public void getAllProperties() throws Exception {

        Set<PropertyForSharingDTO> propertyForSharingDTOS = new HashSet<>(Arrays.asList(
                                                    new PropertyForSharingDTO(), new PropertyForSharingDTO()
                                            ));

        given(propertyForSharingService.getAllProperties()).willReturn(propertyForSharingDTOS);

        mockMvc.perform(get(PropertyForSharingController.BASE_URL + "/all"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/sharing"))
                .andExpect(model().attributeExists("propertiesToShare"));
    }
}