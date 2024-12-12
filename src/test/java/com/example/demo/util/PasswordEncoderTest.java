package com.example.demo.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * 1. 주어진 비밀번호를 인코딩하여 잘 매칭되는지 확인합니다.
 * 2. 주어진 비밀번호와 다른 비밀번호를 인코딩하여 매칭이 않되는지 확인합니다.
 */
public class PasswordEncoderTest {

    @DisplayName("비밀번호 인코딩과 일치 확인")
    @Test
    void passwordEncodeAndMatchTest() {
        // Given
        String password = "password";
        // When
        String encodePassword = PasswordEncoder.encode(password);
        // Then
        Assertions.assertTrue(PasswordEncoder.matches(password, encodePassword));
    }

    @DisplayName("비밀번호 인코딩과 불일치 확인")
    @Test
    void wrongPasswordEncodeAndMatchTest() {
        // Given
        String password = "password";
        // When
        String encodePassword = PasswordEncoder.encode("1234");
        // Then
        Assertions.assertFalse(PasswordEncoder.matches(password, encodePassword));
    }
}
