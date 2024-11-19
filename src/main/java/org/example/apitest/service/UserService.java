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
import org.example.apitest.model.request.changeUserPasswordRequest;
import org.example.apitest.repository.UserRepository;
import org.example.apitest.util.BeanUtilsAdvanced;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;
import java.util.UUID;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    @Autowired
    private JavaMailSender mailSender;

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

    public void sendVerificationEmail(String recipientEmail, String code) {
        String subject = "Xác thực email của bạn";
        String content = "Mã xác thực của bạn là: " + code + "\n\nVui lòng nhập mã này để kích hoạt tài khoản.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(recipientEmail);
        message.setSubject(subject);
        message.setText(content);

        mailSender.send(message);
    }

    public void register(RegisterRequest request) throws ApiException {
        Optional<User> userOptional = userRepository.findUserByUsername(request.getUsername());
        if(userOptional.isPresent()){
            throw new ApiException("Username existed=" + request.getUsername());
        }
        String setVerificationCode = UUID.randomUUID().toString();
        User userRegister = new User(request.getName(), request.getUsername(), encodePassword(request.getPassword()),"USER", request.getPhone(), request.getEmail(), setVerificationCode);
        userRepository.save(userRegister);
        sendVerificationEmail(userRegister.getEmail(), userRegister.getVerificationCode());
    }

    public boolean verifyEmail(String code) {
        Optional<User> userOptional = userRepository.findByVerificationCode(code);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setEnabled(true);
            user.setVerificationCode(null);
            userRepository.save(user);
            return true;
        }
        return false;
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
        user.setName(userRequest.getName());
        user.setPhone(userRequest.getPhone());
        user.setEmail(userRequest.getEmail());
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

    public User changePassword(Long id, changeUserPasswordRequest changeUserPasswordRequest) throws ApiException {
        Optional<User> userExisted = userRepository.findById(id);
        if (!userExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        User user = userExisted.get();
        if (user.getPassword().equals(encodePassword(changeUserPasswordRequest.getPreviousPassword()))) {
            user.setPassword(encodePassword(changeUserPasswordRequest.getNewPassword()));
        } else {
            throw new ApiException("Mật khẩu không trùng khớp=" + id);
        }
        BeanUtilsAdvanced.copyProperties(changeUserPasswordRequest, user);
        return userRepository.save(user);
    }

    public User getUserById(Long id) throws ApiException{
        Optional<User> userExisted = userRepository.findById(id);
        if (!userExisted.isPresent()) {
            throw new ApiException("Not found with id=" + id);
        }
        User user = userExisted.get();
        return user;
    }
}
