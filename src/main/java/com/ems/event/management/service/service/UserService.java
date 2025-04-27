package com.ems.event.management.service.service;

import com.ems.event.management.service.entity.User;

import java.util.Optional;
import java.util.UUID;

public interface UserService {
    Optional<User> getUserById(UUID userId);
}
