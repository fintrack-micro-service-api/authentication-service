package com.example.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDtoClient {
    private UUID id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private String phoneNumber;
    private String profile;
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String notificationType;

//    @JsonInclude(JsonInclude.Include.NON_NULL)
//    private String balance;

}