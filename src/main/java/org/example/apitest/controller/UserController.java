package org.example.apitest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.apitest.dto.JWTTokenDto;
import org.example.apitest.dto.LoginRequestDto;
import org.example.apitest.exception.ApiException;
import org.example.apitest.helper.ResponseBuilder;
import org.example.apitest.model.User;
import org.example.apitest.model.request.RegisterRequest;
import org.example.apitest.model.request.UserRequest;
import org.example.apitest.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
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

    @PostMapping("register")
    public ResponseEntity<ResponseBuilder<User>> register(@Valid @RequestBody RegisterRequest registerRequest) throws ApiException{
        User user = userService.register(registerRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(user, "Register user successfully", HttpStatus.OK));
    }

    @GetMapping("/list")
    public ResponseEntity<ResponseBuilder<Page<User>>> getPageCategories(
            @RequestParam(required = false, defaultValue = "1") int page,
            @RequestParam(required = false, defaultValue = "100") Integer size) {
        Page<User> userPage = userService.getPageUser(page, size);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(userPage, "Get list user successfully", HttpStatus.OK));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<ResponseBuilder<User>> updateUser(
            @PathVariable(name = "id") Long id,
            @RequestBody UserRequest userRequest) throws ApiException {
        User user = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(user, "Update user successfully", HttpStatus.OK));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<ResponseBuilder<Boolean>> deleteUser(@PathVariable(name = "id") Long id) throws ApiException {
        userService.deleteUser(id);
        return ResponseEntity.ok(ResponseBuilder.buildResponse(true, "Delete user successfully", HttpStatus.OK));
    }
}

