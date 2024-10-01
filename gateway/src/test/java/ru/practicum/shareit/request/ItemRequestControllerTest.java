package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Utils.createResponseEntity;

@WebMvcTest(controllers = RequestController.class)

public class ItemRequestControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    RequestController itemRequestService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createNewItemRequest() throws Exception {
        ItemRequestRequestDto itemRequestRequestDto = new ItemRequestRequestDto();
        itemRequestRequestDto.setDescription("ItemRequest description 1");

        ResponseEntity<Object> response = createResponseEntity(itemRequestRequestDto);

        when(itemRequestService.createRequest(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description", is(itemRequestRequestDto.getDescription())));
    }

    @Test
    void readAllItemRequests() throws Exception {
        ItemRequestResponseDto itemRequestResponseDto1 = new ItemRequestResponseDto();
        itemRequestResponseDto1.setDescription("ItemRequest description 1");

        ItemRequestResponseDto itemRequestResponseDto2 = new ItemRequestResponseDto();
        itemRequestResponseDto2.setDescription("ItemRequest description 2");

        List<ItemRequestResponseDto> itemRequestResponseDtos = List.of(
                itemRequestResponseDto1, itemRequestResponseDto2
        );

        ResponseEntity<Object> response = createResponseEntity(itemRequestResponseDtos);

        when(itemRequestService.getAllUserRequests(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[1].description", is(itemRequestResponseDtos.get(1).getDescription())));
    }

    @Test
    void readAllUserItemRequests() throws Exception {
        ItemRequestResponseDto itemRequestResponseDto1 = new ItemRequestResponseDto();
        itemRequestResponseDto1.setDescription("ItemRequest description 1");

        ItemRequestResponseDto itemRequestResponseDto2 = new ItemRequestResponseDto();
        itemRequestResponseDto2.setDescription("ItemRequest description 2");

        List<ItemRequestResponseDto> itemRequestResponseDtos = List.of(
                itemRequestResponseDto1, itemRequestResponseDto2
        );

        ResponseEntity<Object> response = createResponseEntity(itemRequestResponseDtos);

        when(itemRequestService.getAllRequestsOfUsers(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].description", is(itemRequestResponseDtos.get(0).getDescription())))
                .andExpect(jsonPath("$[1].description", is(itemRequestResponseDtos.get(1).getDescription())));
    }

    @Test
    void readItemRequest() throws Exception {
        ItemRequestResponseDto itemRequestResponseDto = new ItemRequestResponseDto();
        itemRequestResponseDto.setId(1L);
        itemRequestResponseDto.setDescription("ItemRequest description 1");

        ResponseEntity<Object> response = createResponseEntity(itemRequestResponseDto);

        when(itemRequestService.read(anyLong()))
                .thenReturn(response);

        mvc.perform(get("/requests/{requestId}", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestResponseDto.getDescription())));
    }
}
