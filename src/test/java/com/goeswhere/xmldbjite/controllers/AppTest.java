package com.goeswhere.xmldbjite.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppTest {
    private MockMvc rest;

    @Before
    public void setup() {
        this.rest = MockMvcBuilders.standaloneSetup(new App()).build();
    }

    @Test
    public void crud() throws Exception {
        rest.perform(
                post("/xml")
                    .content("<a/>")
                )
                .andExpect(status().isOk());
    }
}
