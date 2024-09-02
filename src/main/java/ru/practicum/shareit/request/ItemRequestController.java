package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.Collection;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {
    @PostMapping
    public ItemRequestDto create(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestDto;
    }

    @GetMapping("/{itemRequestId}")
    public ItemRequestDto read(@PathVariable long itemRequestId) {
        return null;
    }

    @GetMapping
    public Collection<ItemRequestDto> readAll() {
        return null;
    }

    @PutMapping
    public ItemRequestDto update(@Valid @RequestBody ItemRequestDto itemRequestDto) {
        return null;
    }

    @DeleteMapping("/{itemRequestId}")
    public void delete(@PathVariable long itemRequestId) {

    }
}
