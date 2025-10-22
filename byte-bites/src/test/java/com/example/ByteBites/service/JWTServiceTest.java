package com.example.ByteBites.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class JWTServiceTest {
    private JWTService jwtService;

    private static final String SECRET_KEY =
            "66556A586E327235753878214125442A472D4B6150645367566B597033733676";
    private static final long ONE_HOUR = 60L * 60 * 1000;

    @BeforeEach
    void setUp() {
        jwtService = new JWTService();
    }

    @Test
    void generateToken_and_extractUsername_and_validate() {
        UserDetails user = User.withUsername("bob")
                .password("doesntmatter")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);
        assertThat(token).isNotEmpty();

        String subject = jwtService.extractUsername(token);
        assertThat(subject).isEqualTo("bob");

        assertThat(jwtService.isTokenValid(token, user)).isTrue();
    }

    @Test
    void extractClaim_expiration_isInFuture() {
        UserDetails user = User.withUsername("alice")
                .password("pwd")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);

        Date exp = jwtService.extractClaim(token, Claims::getExpiration);
        assertThat(exp).isAfter(new Date());
    }

    @Test
    void isTokenValid_returnsFalse_forWrongUser() {
        UserDetails user = User.withUsername("user1")
                .password("pwd")
                .authorities(List.of())
                .build();
        UserDetails other = User.withUsername("user2")
                .password("pwd")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);

        assertThat(jwtService.isTokenValid(token, other)).isFalse();
    }

    @Test
    void isTokenValid_returnsFalse_forExpiredToken() {
        UserDetails user = User.withUsername("expiredUser")
                .password("pwd")
                .authorities(List.of())
                .build();

        Key key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET_KEY));
        String expiredToken = Jwts.builder()
                .setClaims(Map.of())
                .setSubject(user.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis() - 2 * ONE_HOUR))
                .setExpiration(new Date(System.currentTimeMillis() - 1000))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        assertThat(jwtService.isTokenValid(expiredToken, user)).isFalse();
    }

    @Test
    void extractUsername_returnsEmailClaim_whenUsernameLooksLikeEmail() {
        UserDetails user = User.withUsername("jane@example.com")
                .password("pwd")
                .authorities(List.of())
                .build();

        String token = jwtService.generateToken(user);

        String extracted = jwtService.extractUsername(token);
        assertThat(extracted).isEqualTo("jane@example.com");
    }
}
