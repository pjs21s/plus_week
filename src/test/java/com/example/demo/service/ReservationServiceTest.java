package com.example.demo.service;

import com.example.demo.config.JpaConfiguration;
import com.example.demo.constants.Status;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
//@DataJpaTest
//@Import(JpaConfiguration.class)
class ReservationServiceTest {

    @TestConfiguration
    static class MockTestConfig{

        @PersistenceContext
        private EntityManager em;

        @Bean
        public JPAQueryFactory jpaQueryFactory(){
            return new JPAQueryFactory(em);
        }
    }

    @Mock
    private ReservationRepository mockReservationRepository;
    @Mock
    private ItemRepository mockItemRepository;
    @Mock
    private UserRepository mockUserRepository;
    @Mock
    private RentalLogService mockRentalLogService;
//    @Mock
//    private EntityManager mockEntityManager;
    @Mock
    private JPAQueryFactory jpaQueryFactory;
    @Mock
    private JPAQuery<Reservation> query;

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

    @Transactional
    @Test
    void searchAndConvertReservations() {
        // Given
//        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(mockEntityManager);
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation1 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Reservation reservation2 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);
//        mockUserRepository.save(owner);
//        mockUserRepository.save(manager);
//        mockItemRepository.save(item);
//        mockReservationRepository.save(reservation1);
//        mockReservationRepository.save(reservation2);
        // When
        when(jpaQueryFactory.selectFrom((QReservation)any())).thenReturn(query);
        when(query.where(any(Predicate.class))).thenReturn(query);
        when(query.fetch()).thenReturn(reservations);
        List<ReservationResponseDto> results;
        results = mockReservationService.searchAndConvertReservations(1L, 1L);
        // Then
        assertThat(results).isNotNull();
    }

    @Test
    void updateReservationStatus() {
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
}