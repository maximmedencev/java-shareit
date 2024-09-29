package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId,
                                             @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating item {}", itemRequestDto);
        return itemClient.createItem(sharerId, itemRequestDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId) {
        log.info("Getting item with id = {}", itemId);
        return itemClient.getItem(itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItems(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId) {
        log.info("User with id = {} getting all items", sharerId);
        return itemClient.getItems(sharerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable long itemId,
                                         @Positive @RequestHeader("X-Sharer-User-Id") long sharerId,
                                         @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("User with id = {} updating item with id = {}", sharerId, itemId);
        return itemClient.patchItem(itemId, sharerId, itemRequestDto);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItems(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId,
                                              @RequestParam String text) {
        log.info("User with id = {} searching item containing text = {}", sharerId, text);
        return itemClient.searchItems(sharerId, text);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@Positive @RequestHeader("X-Sharer-User-Id") long sharerId,
                                                @PathVariable Long itemId,
                                                @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("User with id = {} creating comment to item  with id = {} {}", sharerId, itemId, commentRequestDto);
        return itemClient.createComment(sharerId, itemId, commentRequestDto);
    }
}
