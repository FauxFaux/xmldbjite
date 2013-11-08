package com.goeswhere.xmldbjite.controllers;

import org.junit.Before;
import org.junit.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AppControllerTest {
    private MockMvc rest;

    @Before
    public void setup() {
        this.rest = MockMvcBuilders.standaloneSetup(new AppController()).build();
    }

    @Test
    public void crud() throws Exception {
        write5();

        rest.perform(
            get("/xml/5")
            )
            .andExpect(status().isOk())
            .andExpect(content().xml("<a id='5'/>"));
    }

    @Test
    public void errors() throws Exception {
        write5();
        post5().andExpect(status().isConflict());

        rest.perform(post("/xml").content("<a/>")).andExpect(status().isBadRequest());
        rest.perform(post("/xml").content("<")).andExpect(status().isBadRequest());
        rest.perform(post("/xml").content("")).andExpect(status().isBadRequest());
    }

    private void write5() throws Exception {
        post5().andExpect(status().isCreated());
    }

    private ResultActions post5() throws Exception {
        return rest.perform(post("/xml").content("<a id='5'/>"));
    }
}
