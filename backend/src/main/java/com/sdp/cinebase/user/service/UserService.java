package com.sdp.cinebase.user.service;

import com.sdp.cinebase.user.dto.UserDto;
import com.sdp.cinebase.user.model.User;
import com.sdp.cinebase.user.repo.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class UserService {
    private final UserRepository repo;

    public UserService(UserRepository repo) {
        this.repo = repo;
    }

    public UserDto getById(String idStr) {
        Long id = Long.valueOf(idStr);
        User u = repo.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return new UserDto(
                u.getId(),
                u.getUsername(),
                u.getEmail(),
                u.getName(),
                u.getSurname(),
                u.getCreatedAt()
        );
    }
}
