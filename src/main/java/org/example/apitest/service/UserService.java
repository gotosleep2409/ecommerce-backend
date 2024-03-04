package org.example.apitest.service;

import lombok.RequiredArgsConstructor;
import org.example.apitest.config.JwtTokenProvider;
import org.example.apitest.dto.JWTTokenDto;
import org.example.apitest.dto.LoginRequestDto;
import org.example.apitest.exception.ApiException;
import org.example.apitest.exception.ErrorMessage;
import org.example.apitest.model.User;
import org.example.apitest.model.request.RegisterRequest;
import org.example.apitest.model.request.UserRequest;
import org.example.apitest.repository.UserRepository;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public JWTTokenDto login(LoginRequestDto loginRequestDto) throws ApiException {
        String username = loginRequestDto.getUsername();
        String password = encodePassword(loginRequestDto.getPassword());
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

    public User register(RegisterRequest request) {
        User userRegister = new User(request.getName(), request.getUsername(), encodePassword(request.getPassword()),"USER" );
        return userRepository.save(userRegister);
    }

    public static String encodePassword(String plainPassword) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(plainPassword.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                hexString.append(String.format("%02x", b));
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Page<User> getPageUser(int page, int size) {
        PageRequest paging = PageRequest.of(page -1, size, Sort.by(Sort.Direction.DESC, "id"));
        return userRepository.findAll(paging);
    }

    public User updateUser(Long id, UserRequest userRequest) throws ApiException {
        Optional<User> userExisted = userRepository.findById(id);
        if (!userExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        User user = userExisted.get();
        BeanUtilsAdvanced.copyProperties(userRequest, user);
        return userRepository.save(user);
    }

    public void deleteUser(Long id) throws ApiException {
        Optional<User> userExisted = userRepository.findById(id);
        if (!userExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        userRepository.delete(userExisted.get());
    }
}
