package com.example.ByteBites.controller;

import com.example.ByteBites.models.AuthRequest;
import com.example.ByteBites.models.RegisterRequest;
import com.example.ByteBites.service.inteface.AuthServiceInterface;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthServiceInterface authService;

    @InjectMocks
    private AuthController authController;

    private HttpServletResponse response;

    @BeforeEach
    void setup() {
        response = mock(HttpServletResponse.class);
    }

    @Test
    void register_shouldReturnSuccessResponse() {
        RegisterRequest request = new RegisterRequest();
        when(authService.register(any())).thenReturn(ResponseEntity.ok("Успешна регистрация"));

        ResponseEntity<String> result = authController.register(request);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Успешна регистрация", result.getBody());
        verify(authService, times(1)).register(request);
    }

    @Test
    void login_shouldReturnJwtAndSetCookie() {
        AuthRequest request = new AuthRequest();
        when(authService.login(any())).thenReturn(ResponseEntity.ok("jwt-token"));

        ResponseEntity<String> result = authController.login(request, response);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("jwt-token", result.getBody());
        verify(authService).login(request);
        verify(response).addCookie(any());
    }

    @Test
    void logout_shouldClearCookie() {
        ResponseEntity<String> result = authController.logout(response);

        assertEquals(200, result.getStatusCodeValue());
        assertEquals("Logged out successfully", result.getBody());
        verify(response).addCookie(any()); // проверка че е изчистено cookie-то
    }
}

