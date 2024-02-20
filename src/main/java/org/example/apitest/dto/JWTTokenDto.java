package org.example.apitest.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.apitest.model.User;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JWTTokenDto {
	private String accessToken;
	private String tokenType;
	private Date expiresAt;
	private User user;
}
