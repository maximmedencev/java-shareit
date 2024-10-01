package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.shareit.request.dto.ItemRequestRequestDto;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class RequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> createRequest(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId,
                                                @Valid @RequestBody ItemRequestRequestDto itemRequestDto) {
        log.info("User with id = {} creating itemRequest = {} ", sharerId, itemRequestDto);
        return itemRequestClient.createRequest(sharerId, itemRequestDto);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequestsOfUsers(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("User with id = {} getting all requests of other users", sharerId);
        return itemRequestClient.getAllRequestsOfUsers(sharerId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllUserRequests(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("User with id = {} getting all self requests", sharerId);
        return itemRequestClient.getAllUserRequests(sharerId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> read(@Positive @PathVariable long requestId) {
        log.info("Getting request with id = {}", requestId);
        return itemRequestClient.getItemRequest(requestId);
    }

}
