package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class UserRequestDto {
    private Long id;
    private String name;
    @Email
    private String email;
}
