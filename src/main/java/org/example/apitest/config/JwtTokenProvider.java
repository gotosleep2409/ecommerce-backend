package org.example.apitest.config;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

import org.example.apitest.constant.AppConstants;
import org.example.apitest.dto.AdminUser;
import org.example.apitest.dto.JWTTokenDto;
import org.example.apitest.model.User;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class JwtTokenProvider {

    public JWTTokenDto generateJWTTokenForUser(User user) {
        long currentTimeStamp = System.currentTimeMillis();
        Date expiresAt = new Date(currentTimeStamp + AppConstants.EXPIRATION_TIME);
        String accessToken = JWT.create()
                .withSubject(user.getId())
                .withIssuedAt(new Date(currentTimeStamp))
                .withExpiresAt(expiresAt)
                .sign(Algorithm.HMAC256(AppConstants.DEFAULT_SECRET.getBytes()));

        return new JWTTokenDto(accessToken, AppConstants.TOKEN_TYPE, expiresAt, user);
    }

    public Date getExpiresAtToken(String token) {
        return JWT.require(Algorithm.HMAC256(AppConstants.DEFAULT_SECRET.getBytes()))
                .build()
                .verify(token)
                .getExpiresAt();
    }

    public String getUserIdFromToken(String token) {
        return JWT.require(Algorithm.HMAC256(AppConstants.DEFAULT_SECRET.getBytes()))
                .build()
                .verify(token)
                .getSubject();
    }
}
