package com.vr.job.service;

import com.vr.job.payload.AuthResponse;
import com.vr.job.payload.LoginRequest;
import com.vr.job.payload.SignupRequest;

public interface AuthService {

    AuthResponse signup(SignupRequest request) throws Exception;
    AuthResponse login(LoginRequest request) throws Exception;
}
