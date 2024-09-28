package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Utils.createResponseEntity;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserClient userClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createNewUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        ResponseEntity<Object> response = createResponseEntity(userDto);

        when(userClient.createUser(any()))
                .thenReturn(response);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void readUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        ResponseEntity<Object> response = createResponseEntity(userDto);

        when(userClient.getUser(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/users/{userId}", userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName())))
                .andExpect(jsonPath("$.email", is(userDto.getEmail())));
    }

    @Test
    void readAllUsers() throws Exception {
        List<UserDto> userDtos = List.of(
                UserDto.builder()
                        .id(1L)
                        .name("Ivan")
                        .email("ivan@mail.ru")
                        .build(),

                UserDto.builder()
                        .id(2L)
                        .name("Petr")
                        .email("petr@mail.ru")
                        .build()
        );
        ResponseEntity<Object> response = createResponseEntity(userDtos);

        when(userClient.getUsers())
                .thenReturn(response);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is(userDtos.get(0).getName())))
                .andExpect(jsonPath("$[1].name", is(userDtos.get(1).getName())));
    }

    @Test
    void updateUser() throws Exception {
        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Semen")
                .email("semen@mail.ru")
                .build();

        ResponseEntity<Object> response = createResponseEntity(updatedUserDto);

        when(userClient.patchUser(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(updatedUserDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUserDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUserDto.getName())))
                .andExpect(jsonPath("$.email", is(updatedUserDto.getEmail())));
    }

    @Test
    void deleteUser() throws Exception {
        mvc.perform(delete("/users/{userId}", anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(userClient, Mockito.times(1))
                .deleteUser(Mockito.anyLong());
    }


}