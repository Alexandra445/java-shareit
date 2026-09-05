package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUserById(long userId) {
        return userRepository.findById(userId);
    }

    @Override
    public User createUser(User user) {
        if (userRepository.findAll().stream()
                .anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            throw new RuntimeException("Email уже используется");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        user.setId(userId);
        return userRepository.update(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}