package org.example.deadlinescheduler.database.service.implementation;

import lombok.AllArgsConstructor;
import org.example.deadlinescheduler.database.model.Deadline;
import org.example.deadlinescheduler.database.model.User;
import org.example.deadlinescheduler.database.repo.UserRepository;
import org.example.deadlinescheduler.database.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<Deadline> getAllDeadlines(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getDeadlines();
    }

    @Override
    public User getUser(String username) {
        return null;
    }

    @Override
    public User saveUser(User user) {
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        // Return existing user
        // Create new user
        return existingUser.orElseGet(() -> userRepository.save(user));

    }
}
