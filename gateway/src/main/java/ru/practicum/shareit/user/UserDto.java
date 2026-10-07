package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserDto {

    private Long id;
    @NotBlank(groups = Create.class, message = "Имя обязательно")
    private String name;
    @NotBlank(groups = Create.class, message = "Email обязателен")
    @Pattern(
            regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$",
            message = "Некорректный email",
            groups = {Create.class, Update.class}
    )
    private String email;

    public interface Create {
    }

    public interface Update {
    }
}