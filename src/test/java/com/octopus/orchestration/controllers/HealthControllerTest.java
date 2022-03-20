package com.octopus.orchestration.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.octopus.orchestration.models.ContainerRequest;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class HealthControllerTest {
    @Autowired
    private MockMvc mockMvc;
    private static URI uri;

    private ContainerRequest containerRequest;

    @BeforeAll
    static void setup() throws URISyntaxException {
        uri = new URI("/health");
    }

    @Test
    void testHealthCheck() throws Exception {
        MvcResult response = mockMvc.perform(get(uri)).andExpect(status().isOk()).andReturn();
        String responseAsString = response.getResponse().getContentAsString();
        assertEquals("healthy", responseAsString);
    }
}
