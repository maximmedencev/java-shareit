package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentParamDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoParam;

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

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;

    @MockBean
    ItemService itemService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createNewItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .build();

        ItemDtoParam itemDtoParam = ItemDtoParam.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .build();

        when(itemService.create(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(itemDtoParam))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoParam.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoParam.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoParam.getDescription())));
    }

    @Test
    void readItem() throws Exception {
        ItemDto itemDto = ItemDto.builder()
                .id(1L)
                .name("Item1 name")
                .description("item1 description")
                .build();

        when(itemService.read(anyLong()))
                .thenReturn(itemDto);

        mvc.perform(get("/items/{itemId}", itemDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())));

    }


    @Test
    void readAllItems() throws Exception {
        List<ItemDto> itemDtos = List.of(

                ItemDto.builder()
                        .id(1L)
                        .name("Item1 name")
                        .description("item1 description")
                        .build(),
                ItemDto.builder()
                        .id(2L)
                        .name("Item2 name")
                        .description("item2 description")
                        .build()
        );

        when(itemService.readAll(anyLong()))
                .thenReturn(itemDtos);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].name", is(itemDtos.get(0).getName())))
                .andExpect(jsonPath("$[1].name", is(itemDtos.get(1).getName())));
    }

    @Test
    void updateItem() throws Exception {

        ItemDtoParam updatedItemDtoParam = ItemDtoParam.builder()
                .id(1L)
                .name("updated Item1 name")
                .description("updated item1 description")
                .build();

        ItemDto updatedItemDto = ItemDto.builder()
                .id(1L)
                .name("updated Item1 name")
                .description("updated item1 description")
                .build();

        when(itemService.update(anyLong(), anyLong(), any()))
                .thenReturn(updatedItemDto);

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

        when(itemService.search(anyLong(), anyString()))
                .thenReturn(List.of(itemDtos.getFirst()));

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
    void createNewComment() throws Exception {

        CommentParamDto commentParamDto = CommentParamDto.builder()
                .id(1L)
                .text("text1")
                .build();

        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("text1")
                .build();

        when(itemService.createComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/{itemId}/comment", 1L)
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(commentParamDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentParamDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentParamDto.getText())));
    }

}
