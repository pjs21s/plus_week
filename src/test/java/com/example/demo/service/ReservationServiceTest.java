package com.example.demo.service;

import com.example.demo.entity.Reservation;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @InjectMocks
    private ReservationService reservationService;

    @Mock
    private ReservationRepository reservationRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;


    @Test
    @DisplayName("유저 생성")
    void createReservationTest() {
        // Given
        Long fakeId = 1L;
        Long itemId = 1L;
        Long userId = 1L;
        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime = LocalDateTime.now().plusMinutes(5);

        // When
        reservationService.createReservation(itemId, userId, startTime, endTime);
        Reservation result = reservationRepository.findById(fakeId).get();

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(fakeId);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
    }

    @Test
    void getReservations() {
        // Given

        // When

        // Then
    }

    @Test
    void searchAndConvertReservations() {
    }

    @Test
    void searchReservations() {
    }

    @Test
    void updateReservationStatus() {
    }
}