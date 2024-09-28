package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createNewUser() throws Exception {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .name("Ivan")
                .email("ivan@mail.ru")
                .build();

        when(userService.create(any()))
                .thenReturn(userDto);

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

        when(userService.read(anyLong()))
                .thenReturn(userDto);

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

        when(userService.readAll())
                .thenReturn(userDtos);

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is(userDtos.get(0).getName())))
                .andExpect(jsonPath("$[1].name", is(userDtos.get(1).getName())));
    }

    @Test
    void updateUser() throws Exception {

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setName("Semen");
        updatedUser.setEmail("semen@mail.ru");

        UserDto updatedUserDto = UserDto.builder()
                .id(1L)
                .name("Semen")
                .email("semen@mail.ru")
                .build();

        when(userService.update(anyLong(), any()))
                .thenReturn(updatedUserDto);

        mvc.perform(patch("/users/{userId}", 1L)
                        .content(mapper.writeValueAsString(updatedUser))
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
        mvc.perform(delete("/users/{userId}", Mockito.anyLong())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .delete(Mockito.anyLong());
    }

}