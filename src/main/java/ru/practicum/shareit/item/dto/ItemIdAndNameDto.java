package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ItemIdAndNameDto {
    private Long id;
    private String name;
}
