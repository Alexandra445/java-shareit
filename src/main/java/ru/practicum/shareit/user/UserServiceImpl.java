package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
        if (user.getEmail() == null || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Некорректный email");
        }

        if (userRepository.findAll().stream().anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email уже используется");
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        User existingUser = userRepository.findById(userId);

        if (existingUser == null) {
            throw new RuntimeException("Пользователь не найден");
        }

        if (user.getEmail() != null) {
            boolean emailAlreadyUsed = userRepository.findAll().stream().anyMatch(existing -> existing.getEmail().equals(user.getEmail()) && existing.getId() != userId);

            if (emailAlreadyUsed) {
                throw new RuntimeException("Email уже используется");
            }
        }

        user.setId(userId);
        return userRepository.update(user);
    }

    @Override
    public void deleteUser(long userId) {
        userRepository.deleteById(userId);
    }
}