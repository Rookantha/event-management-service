package com.ems.event.management.service.service.impl;

import com.ems.event.management.service.entity.User;
import com.ems.event.management.service.repository.UserRepository;
import com.ems.event.management.service.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public Optional<User> getUserById(UUID userId) {
        return userRepository.findById(userId);
    }
}
