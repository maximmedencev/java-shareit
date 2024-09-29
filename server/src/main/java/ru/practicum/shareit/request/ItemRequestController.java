package ru.practicum.shareit.request;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader("X-Sharer-User-Id") long sharerId,
                                 @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.save(sharerId, itemRequestDto);
    }

    @GetMapping("/all")
    public Collection<ItemRequestDto> readAll(@RequestHeader("X-Sharer-User-Id") long sharerId) {
        return itemRequestService.getAllRequestsOfUsers(sharerId);
    }

    @GetMapping
    public Collection<ItemRequestDto> readUserRequests(@RequestHeader("X-Sharer-User-Id") long sharerId) {
        return itemRequestService.getAllUserRequests(sharerId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto read(@PathVariable long requestId) {
        return itemRequestService.getRequest(requestId);
    }

}
