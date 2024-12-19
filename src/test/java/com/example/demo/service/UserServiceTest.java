package com.example.demo.service;

import com.example.demo.dto.Authentication;
import com.example.demo.dto.LoginRequestDto;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.exception.ReservationConflictException;
import com.example.demo.repository.UserRepository;
import com.example.demo.util.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void signupWithEmail() {
        // Given
        UserRequestDto requestDto = new UserRequestDto("user", "user@a.com", "name", "0000");
        // When
        when(userRepository.save(any())).thenReturn(new User());
        userService.signupWithEmail(requestDto);
        // Then
        assertThat(requestDto.getPassword()).isNotEqualTo("0000");
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void loginUserSuccess() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("user@a.com", "0000");
        String encodedPassword = PasswordEncoder.encode(loginRequestDto.getPassword());
        User user = new User("user", "user@a.com", "owner", encodedPassword);
        // When
        when(userRepository.findByEmail(any())).thenReturn(user);
        Authentication authentication = userService.loginUser(loginRequestDto);
        // Then
        assertThat(authentication).isNotNull();
        assertThat(authentication.getRole()).isEqualTo(Role.USER);
    }

    @Test
    void loginUserFailUnauthorizedUser() {
        // Given
        LoginRequestDto loginRequestDto = new LoginRequestDto("user@a.com", "0000");
        // When
        when(userRepository.findByEmail(any())).thenReturn(null);
        // Then
        assertThatThrownBy(() -> userService.loginUser(loginRequestDto))
                .isInstanceOf(ResponseStatusException.class)
                .hasMessageContaining("유효하지 않은 사용자 이름 혹은 잘못된 비밀번호");
    }
}