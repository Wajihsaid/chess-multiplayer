package com.chess.backend.service;

import com.chess.backend.dto.LoginRequest;
import com.chess.backend.dto.RegisterRequest;
import com.chess.backend.entity.User;
import com.chess.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User register(RegisterRequest request) {
        // Vérifier si l'utilisateur existe déjà
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // Créer le nouvel utilisateur (mot de passe en clair pour simplifier)
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword()); // En production: hasher le mot de passe!
        user.setEmail(request.getEmail());
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Vérification simple du mot de passe (en production: utiliser BCrypt)
        if (!user.getPassword().equals(request.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        // Mettre à jour le statut en ligne
        user.setOnline(true);
        user.setLastSeen(LocalDateTime.now());

        return userRepository.save(user);
    }

    public void logout(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    public void setOnline(Long userId, boolean online) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setOnline(online);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    public List<User> getOnlineUsers() {
        return userRepository.findByOnlineTrue();
    }

    public List<User> getOnlineUsersExcept(Long userId) {
        return userRepository.findByOnlineTrueAndIdNot(userId);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}