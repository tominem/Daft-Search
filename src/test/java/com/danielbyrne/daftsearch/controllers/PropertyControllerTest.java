package com.danielbyrne.daftsearch.controllers;

import com.danielbyrne.daftsearch.domain.model.PropertyDTO;
import com.danielbyrne.daftsearch.services.PropertyService;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class PropertyControllerTest {

    @Mock
    PropertyService propertyService;

    @InjectMocks
    PropertyController propertyController;

    MockMvc mockMvc;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(propertyController).build();
    }

    @Test
    public void getAllProperties() throws Exception {

        List<PropertyDTO> propertyDTO = Arrays.asList(new PropertyDTO(), new PropertyDTO());
        given(propertyService.getAllProperties()).willReturn(propertyDTO);

        mockMvc.perform(get("/properties"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/property"))
                .andExpect(model().attributeExists("properties"));
    }
}