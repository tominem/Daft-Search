package com.danielbyrne.daftsearch.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class IndexControllerTest {

    MockMvc mockMvc;
    IndexController indexController;

    @Before
    public void setUp() {
        indexController = new IndexController();
        mockMvc = MockMvcBuilders.standaloneSetup(indexController).build();
    }

    @Test
    public void getIndexPage() throws Exception {

        mockMvc.perform(get("/index"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/index"));

        mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(view().name("property/index"));

        mockMvc.perform(get(""))
                .andExpect(status().isOk())
                .andExpect(view().name("property/index"));
    }
}