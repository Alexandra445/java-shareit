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
        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не найден"
                        )
                );
    }

    @Override
    public User createUser(User user) {
        if (user.getEmail() == null
                || !user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Некорректный email"
            );
        }

        if (userRepository.existsByEmail(user.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Email уже используется"
            );
        }

        return userRepository.save(user);
    }

    @Override
    public User updateUser(long userId, User user) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Пользователь не найден"
                        )
                );

        if (user.getEmail() != null) {
            if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Некорректный email"
                );
            }

            if (!user.getEmail().equals(existingUser.getEmail())
                    && userRepository.existsByEmail(user.getEmail())) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Email уже используется"
                );
            }

            existingUser.setEmail(user.getEmail());
        }

        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }

        return userRepository.save(existingUser);
    }

    @Override
    public void deleteUser(long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Пользователь не найден"
            );
        }

        userRepository.deleteById(userId);
    }
}