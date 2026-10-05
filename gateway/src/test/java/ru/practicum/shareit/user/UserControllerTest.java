package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserClient userClient;

    @Test
    void createUser_shouldReturnUser() throws Exception {
        UserDto input = new UserDto();
        input.setName("Александра");
        input.setEmail("alexandra@test.ru");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setName("Александра");
        result.setEmail("alexandra@test.ru");

        when(userClient.create(any(UserDto.class))).thenReturn(result);

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Александра"));
    }

    @Test
    void createUser_shouldReturn400ForInvalidEmail() throws Exception {
        UserDto input = new UserDto();
        input.setName("Александра");
        input.setEmail("не-email");

        mockMvc.perform(post("/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getAllUsers_shouldReturnUsers() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        when(userClient.getAll()).thenReturn(List.of(user));

        mockMvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Александра"));
    }

    @Test
    void getUser_shouldReturnUser() throws Exception {
        UserDto user = new UserDto();
        user.setId(1L);
        user.setName("Александра");
        user.setEmail("alexandra@test.ru");

        when(userClient.getById(1L)).thenReturn(user);

        mockMvc.perform(get("/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Александра"));
    }

    @Test
    void updateUser_shouldReturnUpdatedUser() throws Exception {
        UserDto input = new UserDto();
        input.setName("Новое имя");

        UserDto result = new UserDto();
        result.setId(1L);
        result.setName("Новое имя");
        result.setEmail("alexandra@test.ru");

        when(userClient.update(any(Long.class), any(UserDto.class)))
                .thenReturn(result);

        mockMvc.perform(patch("/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Новое имя"));
    }

    @Test
    void deleteUser_shouldReturn200() throws Exception {
        mockMvc.perform(delete("/users/1"))
                .andExpect(status().isOk());
    }
}