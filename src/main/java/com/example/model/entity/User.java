package com.example.model.entity;

import com.example.model.dto.UserDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.keycloak.representations.idm.UserRepresentation;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {

    private UUID id;
    private String username;
    private String email;
    private String profile;
    private LocalDateTime createdDate;
    private LocalDateTime lastModified;

    public static UserDto toDto(UserRepresentation userRepresentation, String url) {
        return new UserDto(
                UUID.fromString(userRepresentation.getId()),
                userRepresentation.getUsername(),
                userRepresentation.getEmail(),
                url+userRepresentation.getAttributes().get("profile").get(0),
                LocalDateTime.parse(userRepresentation.getAttributes().get("createdDate").get(0)),
                LocalDateTime.parse(userRepresentation.getAttributes().get("lastModified").get(0))
        );
    }
}

