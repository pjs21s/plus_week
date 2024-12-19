package com.example.demo.repository;

import com.example.demo.dto.ReservationResponseDto;
import com.example.demo.entity.QReservation;
import com.example.demo.entity.Reservation;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class ReservationQueryDslImpl implements ReservationQueryDsl {

    private final JPAQueryFactory jpaQueryFactory;

    @Override
    public List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId) {
        QReservation reservation = QReservation.reservation;
        JPAQuery<Reservation> reservations = jpaQueryFactory.selectFrom(reservation);
        if(userId != null) {
            reservations = reservations.where(reservation.user.id.eq(userId));
        }
        if(itemId != null) {
            reservations = reservations.where(reservation.item.id.eq(itemId));
        }
        List<Reservation> result = reservations.fetch();

        return convertToDto(result);
    }

    private List<ReservationResponseDto> convertToDto(List<Reservation> reservations) {
        return reservations.stream()
                .map(reservation -> new ReservationResponseDto(
                        reservation.getId(),
                        reservation.getUser().getNickname(),
                        reservation.getItem().getName(),
                        reservation.getStartAt(),
                        reservation.getEndAt()
                ))
                .toList();
    }
}
