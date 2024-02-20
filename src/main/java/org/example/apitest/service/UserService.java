package org.example.apitest.service;

import lombok.RequiredArgsConstructor;
import org.example.apitest.config.JwtTokenProvider;
import org.example.apitest.dto.AdminUser;
import org.example.apitest.dto.JWTTokenDto;
import org.example.apitest.dto.LoginRequestDto;
import org.example.apitest.exception.ApiException;
import org.example.apitest.exception.ErrorMessage;
import org.example.apitest.model.User;
import org.example.apitest.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static java.lang.System.console;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JWTTokenDto login(LoginRequestDto loginRequestDto) throws ApiException {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        /*if (!username.equals("admin") || !password.equals("123456")) {
            throw new ApiException(ErrorMessage.CANNOT_LOGIN);
        }

        return jwtTokenProvider.generateJWTTokenForUser(new AdminUser("1", username, password)); */

        User user = userRepository.findByUsername(username);
        if (user == null || !password.equals(user.getPassword())) {
            throw new ApiException(ErrorMessage.CANNOT_LOGIN);
        }
        return jwtTokenProvider.generateJWTTokenForUser(user);
    }
}
