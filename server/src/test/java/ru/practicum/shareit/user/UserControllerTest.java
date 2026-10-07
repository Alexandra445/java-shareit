package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    void create() throws Exception {
        User user = new User();
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        when(userService.createUser(org.mockito.ArgumentMatchers.any(User.class)))
                .thenReturn(user);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Новое имя\",\"email\":\"new@test.ru\"}"))
                .andExpect(status().isOk());

        verify(userService).createUser(org.mockito.ArgumentMatchers.any(User.class));
    }

    @Test
    void getAll() throws Exception {
        User user = new User();
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        when(userService.getAllUsers())
                .thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk());

        verify(userService).getAllUsers();
    }

    @Test
    void getById() throws Exception {
        User user = new User();
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        when(userService.getUserById(1L))
                .thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk());

        verify(userService).getUserById(1L);
    }

    @Test
    void update() throws Exception {
        User user = new User();
        user.setName("Новое имя");
        user.setEmail("new@test.ru");

        when(userService.updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(User.class)
        )).thenReturn(user);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Александра\",\"email\":\"alexandra@test.ru\"}"))
                .andExpect(status().isOk());

        verify(userService).updateUser(
                org.mockito.ArgumentMatchers.eq(1L),
                org.mockito.ArgumentMatchers.any(User.class)
        );
    }

    @Test
    void delete() throws Exception {
        mockMvc.perform(
                org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete("/users/1")
        ).andExpect(status().isOk());

        verify(userService).deleteUser(1L);
    }
}