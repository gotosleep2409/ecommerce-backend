package org.example.apitest.dto;

import lombok.Data;

@Data
public class AdminUser {
    private String id;
    private String username;
    private String password;

    public AdminUser(String id, String username, String password) {
        this.id = id;
        this.username = username;
        this.password = password;
    }
}
