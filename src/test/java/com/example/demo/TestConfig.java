package com.example.demo;

import com.example.demo.repository.ItemRepository;
import com.example.demo.repository.ReservationRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.RentalLogService;
import com.example.demo.service.ReservationService;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.convention.TestBean;
// https://stir.tistory.com/407
@TestConfiguration
public class TestConfig {
    @Bean
    public TestBean testReservationService() {
        return new ReservationService();
    }

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final RentalLogService rentalLogService;

    @Bean
    public ReservationRepository reservationRepository() {
        return new ReservationRepository();
    }
}
