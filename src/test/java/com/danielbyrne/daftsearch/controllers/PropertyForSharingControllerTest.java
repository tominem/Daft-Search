package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyForSharingDTO;
import com.danielbyrne.daftsearch.services.PropertyForSharingService;
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

public class PropertyForSharingControllerTest {

    @Mock
    PropertyForSharingService propertyForSharingService;

    @InjectMocks
    PropertyForSharingController propertyForSharingController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyForSharingController).build();
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