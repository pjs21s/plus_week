package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@WebMvcTest(controllers = { AdminController.class })
class AdminControllerTest {

    @Autowired
    private MockMvc mvc;

    @Test
    void reportUsers() {
//        mockMvc.perform(get("/api/hello"))
//                .andExpect(status().isOk())
//                .andExpect();
    }
}