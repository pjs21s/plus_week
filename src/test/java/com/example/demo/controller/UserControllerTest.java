package com.example.demo.controller;

import com.example.demo.constants.GlobalConstants;
import com.example.demo.dto.Authentication;
import com.example.demo.dto.UserRequestDto;
import com.example.demo.entity.Role;
import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("회원가입")
    void signupWithEmail() throws Exception {
        // Given
        UserRequestDto requestDto = new UserRequestDto("USER", "uer@a.com", "user", "0000");
        // When
        // Then
        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                        {
                            "email": "test@example.com",
                            "nickname": "name",
                            "password": "0000",
                            "role": "USER"
                        }
                        """))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("로그인 테스트")
    void loginWithEmail() throws Exception {
        // Given
        Authentication authentication = new Authentication(1L, Role.USER);
        MockHttpSession session = new MockHttpSession();
        // When
        when(userService.loginUser(any())).thenReturn(authentication);
//        when(request.getSession()).thenReturn(session);
        // Then
        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                            "email": "test@example.com",
                            "password": "0000"
                        }
                        """)
                        .session(session))
                .andExpect(status().isOk());
        assertThat(session).isNotNull();
        Authentication authen = (Authentication) session.getAttribute(GlobalConstants.USER_AUTH);
        assertThat(authen.getRole()).isEqualTo(authentication.getRole());
    }

    @Test
    @DisplayName("로그아웃 테스트")
    void logout() throws Exception {
        // Given
        MockHttpSession session = new MockHttpSession();
        Authentication authentication = new Authentication(1L, Role.USER);
        session.setAttribute(GlobalConstants.USER_AUTH, authentication);
        // When

        // Then
        mockMvc.perform(post("/users/logout")
                        .session(session))
                .andExpect(status().isOk());
    }
}