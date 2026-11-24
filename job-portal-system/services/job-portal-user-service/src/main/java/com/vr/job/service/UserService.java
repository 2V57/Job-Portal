package com.vr.job.service;

import com.vr.job.dto.response.UserResponse;
import com.vr.job.modal.User;
import com.vr.job.payload.UpdateUserRequest;

import java.util.List;

public interface UserService {

    User getUserByEmail(String email) throws Exception;

    User getUserById(Long id) throws Exception;

    List<User> getAllUsers();

    UserResponse updateProfile(String email, UpdateUserRequest request) throws Exception;

    // admin action

    UserResponse suspendUser(Long id) throws Exception;

    UserResponse activateUser(Long id) throws Exception;

    UserResponse deleteUser(Long id) throws Exception;

}
