package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import ru.practicum.shareit.item.dto.ItemIdAndNameDto;
import ru.practicum.shareit.user.dto.UserIdOnlyDto;

import java.time.LocalDateTime;
import java.util.Collection;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ItemRequestDto {
    private long id;
    private String description;
    private UserIdOnlyDto requestor;
    private LocalDateTime created;
    private Collection<ItemIdAndNameDto> items;

}
