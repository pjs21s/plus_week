package com.example.demo.controller;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.ReservationRequestDto;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.service.ReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.mapper.Mapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
//import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@AutoConfigureMockMvc(addFilters = false)
public class ReservationControllerTest {

    private MockHttpSession session;

//    @Autowired
//    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

//    @Autowired
//    private ReservationService reservationService;

    @BeforeEach
    public void setUp() {
        session = new MockHttpSession();
        session.setAttribute(GlobalConstants.USER_AUTH, "test");
    }

    @Test
    @DisplayName("예약 생성")
    void createReservationTest() throws Exception {


        // Given
//        ReservationRequestDto reservationRequestDto = new ReservationRequestDto(1L, 1L, LocalDateTime.now(), LocalDateTime.now());
//        ReservationResponseDto responseDto = new ReservationResponseDto(1L, "name", "item", LocalDateTime.now(), LocalDateTime.now());

//        given(reservationService.createReservation(1L, 1L, LocalDateTime.now(), LocalDateTime.now())).willReturn(responseDto);
//        doNothing().when(reservationService).createReservation(anyLong(), anyLong(), any(), any());

        //  & Then1``11
        mockMvc.perform(post("/reservations")
//                    .content(mapper.writeValueAsString(reservationRequestDto))
                    .contentType(MediaType.APPLICATION_JSON)
                    .session(session))
                .andExpect(status().isOk());
//                .andExpect(jsonPath("$.id").value(1))
//                .andExpect(jsonPath("$.name").value("name"))
//                .andExpect(jsonPath("$.item").value("item"));


    }

    @Test
    void updateReservationTest() {
    }

    @Test
    void findAllTest() {
    }

    @Test
    void searchAllTest() {
//        // Given
//        String keyword = "test";
//        List<ReservationResponseDto> responseList = List.of(new ReservationResponseDto(/* 초기화 값 */));
//        given(reservationService.searchAll(keyword)).willReturn(responseList);
//        // When
//        // Then
//        mockMvc.perform(get("/reservations/search")
//                        .param("keyword", keyword)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.length()").value(responseList.size()));
    }
}

