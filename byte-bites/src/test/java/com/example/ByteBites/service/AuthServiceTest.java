package com.example.ByteBites.service;

import com.example.ByteBites.models.Accounts;
import com.example.ByteBites.models.AuthRequest;
import com.example.ByteBites.models.RegisterRequest;
import com.example.ByteBites.models.Roles;
import com.example.ByteBites.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AccountRepository userRepository;
    @Mock
    private JWTService jwtService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    private RegisterRequest registerRequest;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setUsername("user1");
        registerRequest.setEmail("user1@example.com");
        registerRequest.setPassword("pass");
        registerRequest.setPhoneNumber("123456789");
    }

    @Test
    void register_whenUsernameExists_thenBadRequest() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(new Accounts()));
        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_whenEmailExists_thenBadRequest() {
        when(userRepository.findByUsername("user1")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("user1@example.com")).thenReturn(Optional.of(new Accounts()));
        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("already taken"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void register_success_defaultsToUserRole() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode("pass")).thenReturn("encodedPass");

        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("role: USER"));
        verify(userRepository).save(argThat(user ->
                user.getUsername().equals("user1") &&
                        user.getEmail().equals("user1@example.com") &&
                        user.getPassword().equals("encodedPass") &&
                        user.getRole() == Roles.USER
        ));
    }

    @Test
    void register_success_withCustomRole() {
        registerRequest.setRole(Roles.OWNER);
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(any())).thenReturn("enc");

        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getBody().contains("role: OWNER"));
        verify(userRepository).save(argThat(user -> user.getRole() == Roles.OWNER));
    }

    @Test
    void register_whenDataIntegrityViolation_thenBadRequest() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        doThrow(DataIntegrityViolationException.class).when(userRepository).save(any());

        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertTrue(response.getBody().contains("already taken"));
    }

    @Test
    void register_whenUnexpectedException_thenServerError() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        doThrow(new RuntimeException("DB down")).when(userRepository).save(any());

        ResponseEntity<String> response = authService.register(registerRequest);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Registration failed: DB down"));
    }

    @Test
    void login_whenUserNotFound_thenUnauthorized() {
        AuthRequest authReq = new AuthRequest();
        authReq.setIdentifier("unknown");
        authReq.setPassword("pw");
        when(userRepository.findByUsername("unknown")).thenReturn(Optional.empty());
        when(userRepository.findByEmail("unknown")).thenReturn(Optional.empty());

        ResponseEntity<String> response = authService.login(authReq);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid username or password"));
    }

    @Test
    void login_whenAuthFails_thenUnauthorized() {
        AuthRequest authReq = new AuthRequest();
        authReq.setIdentifier("user1");
        authReq.setPassword("pw");
        Accounts user = new Accounts();
        user.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));

        doThrow(new AuthenticationException("bad creds") {})
                .when(authenticationManager)
                .authenticate(any(UsernamePasswordAuthenticationToken.class));

        ResponseEntity<String> response = authService.login(authReq);

        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertTrue(response.getBody().contains("Invalid username or password"));
    }

    @Test
    void login_success_returnsJwt() {
        AuthRequest authReq = new AuthRequest();
        authReq.setIdentifier("user1");
        authReq.setPassword("pw");
        Accounts user = new Accounts(); user.setUsername("user1");

        when(userRepository.findByUsername("user1")).thenReturn(Optional.of(user));
        when(jwtService.generateToken(user)).thenReturn("jwt-token");

        ResponseEntity<String> response = authService.login(authReq);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("jwt-token", response.getBody());
    }

    @Test
    void login_whenUnexpectedException_thenServerError() {
        AuthRequest authReq = new AuthRequest();
        authReq.setIdentifier("user1");
        authReq.setPassword("pw");

        Accounts user = new Accounts();
        user.setUsername("user1");
        when(userRepository.findByUsername("user1"))
                .thenReturn(Optional.of(user));

        when(authenticationManager.authenticate(
                any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new RuntimeException("DB error"));

        ResponseEntity<String> response = authService.login(authReq);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertTrue(response.getBody().contains("Login failed: DB error"));
    }
}

