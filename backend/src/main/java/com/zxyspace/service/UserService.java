package com.zxyspace.service;

import com.zxyspace.dto.UserDto;
import com.zxyspace.model.User;

import java.util.List;

public interface UserService {
    
    UserDto getUserById(Long id);
    
    UserDto getUserByUsername(String username);
    
    UserDto getUserByEmail(String email);
    
    List<UserDto> getAllUsers();
    
    UserDto createUser(User user);
    
    UserDto updateUser(Long id, User userDetails);
    
    void deleteUser(Long id);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
} 