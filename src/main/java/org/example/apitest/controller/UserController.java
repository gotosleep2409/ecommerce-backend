package org.example.apitest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apitest.dto.JWTTokenDto;
import org.example.apitest.dto.LoginRequestDto;
import org.example.apitest.exception.ApiException;
import org.example.apitest.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<JWTTokenDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto) throws ApiException {
        JWTTokenDto jwtTokenDto = userService.login(loginRequestDto);
        return ResponseEntity.ok(jwtTokenDto);
    }
}

