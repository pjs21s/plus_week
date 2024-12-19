package com.example.demo.config;

import com.example.demo.repository.ReservationQueryDslImpl;
import com.example.demo.repository.ReservationRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

// https://stir.tistory.com/407
@TestConfiguration
public class TestConfig {
    @PersistenceContext
    private EntityManager em;

    @Bean
    public JPAQueryFactory jpaQueryFactory() {
        return new JPAQueryFactory(em);
    }

    @Bean
    public ReservationQueryDslImpl reservationQueryDslImpl() {
        return new ReservationQueryDslImpl(jpaQueryFactory());
    }
}
