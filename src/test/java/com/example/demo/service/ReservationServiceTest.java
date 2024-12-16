package com.example.demo.service;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.constants.Status;
import com.example.demo.dto.Authentication;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReservationServiceTest {

    @Mock
    private ReservationRepository mockReservationRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RentalLogService mockRentalLogService;

    @InjectMocks
    private ReservationService mockReservationService;

    @Test
    @DisplayName("유저 생성")
    void createReservationTest() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());

        // When
        when(mockItemRepository.findById(any())).thenReturn(Optional.of(item));
        when(mockUserRepository.findById(any())).thenReturn(Optional.of(owner));
        when(mockReservationRepository.save(any())).thenReturn(reservation);
        ReservationResponseDto result;
        result = mockReservationService.createReservation(
                reservation.getItem().getId(),
                reservation.getUser().getId(),
                reservation.getStartAt(),
                reservation.getEndAt()
        );

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getNickname()).isEqualTo(reservation.getUser().getNickname());
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