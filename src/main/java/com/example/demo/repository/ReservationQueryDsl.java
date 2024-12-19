package com.example.demo.repository;

import com.example.demo.dto.ReservationResponseDto;

import java.util.List;

public interface ReservationQueryDsl {
    List<ReservationResponseDto> searchAndConvertReservations(Long userId, Long itemId);
}
