package com.example.controller;

import com.example.model.request.ProfileRequest;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.UUID;


@RestController
@CrossOrigin
@RequestMapping("api/v1/users")

public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("username")
    @Operation(summary = "get user by username")
    public ResponseEntity<?> getByUsername(@RequestParam String username) {
        return ResponseEntity.ok().body(userService.getByUserName(username));
    }

    @GetMapping("email")
    @Operation(summary = "get user by email")
    public ResponseEntity<?> getByEmail(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.getByEmail(email));
    }

//    @GetMapping
    public ResponseEntity<?> getAllUser() {
        return ResponseEntity.ok().body(userService.getAllUsers());
    }

    @GetMapping("/{id}")
    @Operation(summary = "get user by id (UUID) ")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        return ResponseEntity.ok().body(userService.getById(id));
    }
    @GetMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "get user information ")
    public ResponseEntity<?> getUserInfo(Principal principal) {
        return ResponseEntity.ok().body(userService.getInfo(principal));
    }

    @PutMapping
    @SecurityRequirement(name = "auth")
    @Operation(summary = "update information user current user  ")
    public ResponseEntity<?> updateById(@RequestBody ProfileRequest userRequest, Principal principal,@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok().body(userService.updateById(userRequest, principal,jwt));
    }

    @GetMapping("/after-login")
    @SecurityRequirement(name = "auth")
    @Operation(summary = "set Attributes when login git & google ")
    public ResponseEntity<?> updateUserWhenLoginGit(Principal principal) {
        return ResponseEntity.ok().body(userService.updateUserWhenLoginGit(principal));
    }


}
