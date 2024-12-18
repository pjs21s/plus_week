package com.example.demo.repository;

import com.example.demo.config.TestConfig;
import com.example.demo.constants.Status;
import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.Item;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.Reservation;
import com.example.demo.entity.User;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@DataJpaTest
@Import(TestConfig.class)
class ReservationQueryDslImplTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private JPAQueryFactory jpaQueryFactory;

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private ReservationQueryDslImpl reservationQueryDslImpl;

    @Transactional
    @Test
    void searchAndConvertReservations() {
        // Given
        User owner = new User("user", "owner@a.com", "owner", "0000");
        User manager = new User("user", "manager@a.com", "manager", "0000");
        Item item = new Item("testItem", "item description", manager, owner);
        Reservation reservation1 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        Reservation reservation2 = new Reservation(item, owner, Status.PENDING, LocalDateTime.now(), LocalDateTime.now());
        List<Reservation> reservations = new ArrayList<>();
        reservations.add(reservation1);
        reservations.add(reservation2);
        owner = userRepository.save(owner);
        userRepository.save(manager);
        item = itemRepository.save(item);
        reservationRepository.save(reservation1);
        reservationRepository.save(reservation2);
        // When
        List<ReservationResponseDto> results = reservationQueryDslImpl.searchAndConvertReservations(owner.getId(), item.getId());
        // Then
        assertEquals(reservations.size(), results.size());
    }
}