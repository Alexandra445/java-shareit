package ru.practicum.shareit.user;

import org.springframework.validation.annotation.Validated;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public UserDto create(@Validated(UserDto.Create.class) @RequestBody UserDto userDto) {
        return userClient.create(userDto);
    }

    @GetMapping
    public List<UserDto> getAll() {
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getById(@PathVariable long userId) {
        return userClient.getById(userId);
    }

    @PatchMapping("/{userId}")
    public UserDto update(
            @PathVariable long userId,
            @Validated(UserDto.Update.class) @RequestBody UserDto userDto) {

        return userClient.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable long userId) {
        userClient.delete(userId);
    }
}