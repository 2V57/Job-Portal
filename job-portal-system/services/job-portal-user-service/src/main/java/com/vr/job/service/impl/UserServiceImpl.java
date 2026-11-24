package com.vr.job.service.impl;

import com.vr.job.domain.UserStatus;
import com.vr.job.dto.response.UserResponse;
import com.vr.job.mapper.UserMapper;
import com.vr.job.modal.User;
import com.vr.job.payload.UpdateUserRequest;
import com.vr.job.repository.UserRepository;
import com.vr.job.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User getUserByEmail(String email) throws Exception {
        User user = userRepository.findByEmail(email);

        if (user == null){
            throw new Exception("User not found " + email);
        }

        return user;
    }

    @Override
    public User getUserById(Long id) throws Exception {
        return userRepository.findById(id).orElseThrow(
                () -> new Exception("User not found")
        );
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public UserResponse updateProfile(String email, UpdateUserRequest request) throws Exception {

        User user = getUserByEmail(email);

        if(request.getFullName() != null){
            user.setFullName(request.getFullName());
        }

        if (request.getPhone() != null){
            user.setPhone(request.getPhone());
        }

        if(request.getProfileImage() != null){
            user.setProfileImage(request.getProfileImage());
        }

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserResponse suspendUser(Long id) throws Exception {

        User user = getUserById(id);
        user.setStatus(UserStatus.SUSPENDED);
        user.setSuspendedAt(LocalDateTime.now());

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserResponse activateUser(Long id) throws Exception {

        User user = getUserById(id);
        user.setStatus(UserStatus.ACTIVE);
        user.setSuspendedAt(null);

        return UserMapper.toDTO(userRepository.save(user));
    }

    @Override
    public UserResponse deleteUser(Long id) throws Exception {

        User user = getUserById(id);
        user.setStatus(UserStatus.DELETED);
        user.setDeleteAt(LocalDateTime.now());

        return UserMapper.toDTO(userRepository.save(user));
    }
}
