package com.example.demo.controller;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Role;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

    private MockHttpSession session;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        Authentication authentication = new Authentication(1L, Role.USER);
        session = new MockHttpSession();
        session.setAttribute(GlobalConstants.USER_AUTH, authentication);
    }

    @Test
    @DisplayName("예약 생성")
    void createReservationTest() throws Exception {
        // Given
        ReservationRequestDto reservationRequestDto = new ReservationRequestDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now());
        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "name", "item", LocalDateTime.now(), LocalDateTime.now());

        given(reservationService.createReservation(1L, 1L, LocalDateTime.now(), LocalDateTime.now())).willReturn(responseDto);

        //  & Then1
        mockMvc.perform(post("/reservations")
                    .content(mapper.writeValueAsString(reservationRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void updateReservationTest() throws Exception {
        // Given
        String status = "PENDING";
        Long id = 1L;
        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "name", "item", LocalDateTime.now(), LocalDateTime.now());
        // When
        given(reservationService.updateReservationStatus(1L, status)).willReturn(responseDto);
        // Then
        mockMvc.perform(
                patch("/reservations/{id}/update-status", id)
                        .content(status)
//                        .contentType(String.class)
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void findAllTest() throws Exception {
        // Given

        // When

        // Then
        mockMvc.perform(get("/reservations")
                        .session(session))
                .andExpect(status().isOk());
    }

    @Test
    void searchAllTest() throws Exception {
        // Given
        Long userId = 1L;
        Long itemId = 1L;
//        given(reservationService.searchAndConvertReservations(userId, itemId)).willReturn(null);
        // When
        // Then
        mockMvc.perform(get("/reservations/search")
//                        .param("userId", userId.toString())
//                        .param("itemId", itemId.toString())
                        .session(session))
                .andExpect(status().isOk());
    }
}
