package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.Utils.createResponseEntity;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemClient itemClient;

    @Autowired
    private MockMvc mvc;

    @Test
    void createItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .available(true)
                .build();

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .available(true)
                .build();

        ResponseEntity<Object> response = createResponseEntity(itemDto);

        when(itemClient.createItem(anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemRequestDto.getName())))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())));
    }

    @Test
    void getItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .build();

        ResponseEntity<Object> response = createResponseEntity(itemDto);

        when(itemClient.getItem((anyLong())))
                .thenReturn(response);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

    }

    @Test
    void updateItem() throws Exception {

        ItemRequestDto updatedItemDtoParam = ItemRequestDto.builder()
                .id(1L)
                .name("updated Item1 name")
                .description("updated item1 description")
                .available(true)
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("updated Item1 name")
                .description("updated item1 description")
                .available(true)
                .build();

        ResponseEntity<Object> response = createResponseEntity(updatedItemDto);

        when(itemClient.patchItem(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mvc.perform(patch("/items/{itemId}", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(updatedItemDtoParam))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedItemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedItemDto.getName())))
                .andExpect(jsonPath("$.description", is(updatedItemDto.getDescription())));
    }

    @Test
    void searchItems() throws Exception {
        String searchString = "item1";

        List<ItemDto> itemDtos = List.of(
                ItemDto.builder()
                        .id(1L)
                        .name("Item1 name")
                        .description("item1 description")
                        .build()
        );

        ResponseEntity<Object> response = createResponseEntity(List.of(itemDtos.getFirst()));

        when(itemClient.searchItems(anyLong(), anyString())).thenReturn(response);

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", searchString)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].name", is(itemDtos.get(0).getName())));
    }

    @Test
    void createComment() throws Exception {
        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .id(1L)
                .text("text1")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text1")
                .build();

        ResponseEntity<Object> response = createResponseEntity(commentDto);

        when(itemClient.createComment(anyLong(), anyLong(), any()))
                .thenReturn(response);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentRequestDto.getText())));
    }
}
