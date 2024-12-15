package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReservationRequestDto {
    private Long itemId;
    private Long userId;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
}
