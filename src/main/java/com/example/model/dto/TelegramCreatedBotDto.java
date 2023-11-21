package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TelegramCreatedBotDto {
    private Long id;
    private String botUsername;
    private String botToken;
    private String botLink;
}
