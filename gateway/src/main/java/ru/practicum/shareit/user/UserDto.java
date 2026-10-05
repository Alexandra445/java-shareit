package ru.practicum.shareit.user;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {

    private Long id;

    private String name;

    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Некорректный email"
    )
    private String email;
}