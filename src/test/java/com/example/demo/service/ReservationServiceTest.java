package com.example.demo.service;

import com.example.demo.constants.Status;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
    @DisplayName("예약 생성 성공")
    void createReservationTestSuccess() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // When
        when(mockReservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
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
    @DisplayName("예약 생성 실패( 해당시간에 예약이 있음 )")
    void createReservationTestFailedAlready() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> list = new ArrayList<>();
        list.add(reservation);
        // When
        when(mockReservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(list);
        // Then
        assertThatThrownBy(() -> mockReservationService.createReservation(1L, 1L, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(ReservationConflictException.class)
                .hasMessageContaining("해당 물건은 이미 그 시간에 예약이 있습니다.");
    }

    @Test
    @DisplayName("예약 생성 실패 아이템 아이디를 못 찾음")
    void createReservationTestFailNotFoundItem() {
        // Given
        // When
        when(mockReservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());

        // Then
        assertThatThrownBy(() -> mockReservationService.createReservation(null, 1L, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID에 맞는 값이 존재하지 않습니다.");
    }

    @Test
    @DisplayName("예약 생성 실패 유저 아이디를 못 찾음")
    void createReservationTestFailNotFoundUser() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // When
        when(mockReservationRepository.findConflictingReservations(any(), any(), any())).thenReturn(List.of());
        when(mockItemRepository.findById(any())).thenReturn(Optional.of(item));

        // Then
        assertThatThrownBy(() -> mockReservationService.createReservation(1L, null, LocalDateTime.now(), LocalDateTime.now()))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("해당 ID에 맞는 값이 존재하지 않습니다.");
    }

    @Test
    void getReservations() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        // When
        when(mockReservationRepository.findAll()).thenReturn(List.of(reservation));
        List<ReservationResponseDto> results;
        results = mockReservationService.getReservations();
        // Then
        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(1);
        assertThat(results.get(0).getNickname()).isEqualTo(reservation.getUser().getNickname());
    }

    @Test
    void searchAndConvertReservationsSuccess() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation1 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Reservation reservation2 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);
        // When
        List<ReservationResponseDto> results;
        results = mockReservationService.searchAndConvertReservations(1L, 1L);
        // Then
        assertThat(results).isNotNull();
    }

    @Test
    void searchAndConvertReservationsUserIdIsNull() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation1 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Reservation reservation2 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);
        // When
        List<ReservationResponseDto> results;
        results = mockReservationService.searchAndConvertReservations(null, 1L);
        // Then
        assertThat(results).isNotNull();
    }

    @Test
    void searchAndConvertReservationsItemIdIsNull() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation1 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Reservation reservation2 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);
        // When
        List<ReservationResponseDto> results;
        results = mockReservationService.searchAndConvertReservations(1L, null);
        // Then
        assertThat(results).isNotNull();
    }

    @Test
    void updateReservationStatusSuccess() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        String status = "CANCELED";
        // When
        when(mockReservationRepository.findByIdOrElseThrow(1L)).thenReturn(reservation);
        ReservationResponseDto result = mockReservationService.updateReservationStatus(1L, status);
        // Then
        assertThat(result).isNotNull();
    }

    @Test
    void updateReservationStatusFailStatus() {
        // Given
        String status = "CANCELE";  // 잘못 된 입력
        // When
        // Then
        assertThatThrownBy(() -> mockReservationService.updateReservationStatus(1L, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("올바르지 않은 상태: " + status.toUpperCase());
    }

    @Test
    void updateReservationStatusNotPending() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.CANCELED, LocalDateTime.now(), LocalDateTime.now());
        String status = "APPROVED";  // 잘못 된 입력
        // When
        when(mockReservationRepository.findByIdOrElseThrow(1L)).thenReturn(reservation);
        // Then
        assertThatThrownBy(() -> mockReservationService.updateReservationStatus(1L, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("PENDING 상태만 " + status + "로 변경 가능합니다.");
    }

    @Test
    void updateReservationStatusNotExpired() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation = new Reservation(item, owner, Status.EXPIRED, LocalDateTime.now(), LocalDateTime.now());
        String status = "CANCELED";  // 잘못 된 입력
        // When
        when(mockReservationRepository.findByIdOrElseThrow(1L)).thenReturn(reservation);
        // Then
        assertThatThrownBy(() -> mockReservationService.updateReservationStatus(1L, status))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("EXPIRED 상태인 예약은 취소할 수 없습니다.");
    }
}