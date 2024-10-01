package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingParamDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {

    @Autowired
    ObjectMapper mapper;

    @MockBean
    BookingService bookingService;

    @Autowired
    private MockMvc mvc;

    @Test
    void createBooking() throws Exception {
        BookingParamDto bookingParamDto = new BookingParamDto();
        bookingParamDto.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingParamDto.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingParamDto.setStatus(BookingStatus.APPROVED);

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingDto.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingDto.setStatus(BookingStatus.APPROVED);


        when(bookingService.save(anyLong(), any()))
                .thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(mapper.writeValueAsString(bookingParamDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void approveBooking() throws Exception {

        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingDto.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.setApproved(anyLong(), anyLong(), eq(true)))
                .thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.status", is(BookingStatus.APPROVED.toString())));
    }

    @Test
    void readBooking() throws Exception {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingDto.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingDto.setStatus(BookingStatus.APPROVED);

        when(bookingService.read(anyLong(), anyLong()))
                .thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(bookingDto.getStart().toString())))
                .andExpect(jsonPath("$.end", is(bookingDto.getEnd().toString())))
                .andExpect(jsonPath("$.status", is(bookingDto.getStatus().toString())));
    }

    @Test
    void readAllByBookerId() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingDto1.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingDto1.setStatus(BookingStatus.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setStart(LocalDateTime.of(2024, 7, 7, 7, 7, 7));
        bookingDto2.setEnd(LocalDateTime.of(2024, 8, 8, 8, 8, 8));
        bookingDto2.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookingDtos = List.of(bookingDto1, bookingDto2);

        when(bookingService.readAllByBookerId(anyLong(), eq(BookingsState.ALL)))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", BookingsState.ALL.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtos.get(0).getStart().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingDtos.get(1).getStart().toString())));
    }

    @Test
    void readAllByOwnerId() throws Exception {
        BookingDto bookingDto1 = new BookingDto();
        bookingDto1.setId(1L);
        bookingDto1.setStart(LocalDateTime.of(2024, 5, 5, 5, 5, 5));
        bookingDto1.setEnd(LocalDateTime.of(2024, 6, 6, 6, 6, 6));
        bookingDto1.setStatus(BookingStatus.APPROVED);

        BookingDto bookingDto2 = new BookingDto();
        bookingDto2.setId(1L);
        bookingDto2.setStart(LocalDateTime.of(2024, 7, 7, 7, 7, 7));
        bookingDto2.setEnd(LocalDateTime.of(2024, 8, 8, 8, 8, 8));
        bookingDto2.setStatus(BookingStatus.APPROVED);

        List<BookingDto> bookingDtos = List.of(bookingDto1, bookingDto2);

        when(bookingService.readAllByOwnerId(anyLong(), eq(BookingsState.ALL)))
                .thenReturn(bookingDtos);

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", BookingsState.ALL.toString())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2)))
                .andExpect(jsonPath("$[0].id", is(bookingDtos.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(bookingDtos.get(0).getStart().toString())))
                .andExpect(jsonPath("$[1].id", is(bookingDtos.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].start", is(bookingDtos.get(1).getStart().toString())));
    }
}
