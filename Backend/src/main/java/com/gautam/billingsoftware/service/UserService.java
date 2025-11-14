package com.gautam.billingsoftware.service;

import com.gautam.billingsoftware.io.UserRequest;
import com.gautam.billingsoftware.io.UserResponse;

import java.util.List;

public interface UserService {
    UserResponse createUser(UserRequest request);

    String getUserRole(String email);

    List<UserResponse> readUsers();

    void deleteUser(String id);

}
