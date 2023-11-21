package com.example.controller;


import com.example.model.request.LoginRequest;
import com.example.model.request.UserRequest;
import com.example.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;


@RestController
@RequestMapping("api/v1/auth")
@CrossOrigin
public class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    @Operation(summary = "register")
    public ResponseEntity<?> createUser(@RequestBody UserRequest userRequest) {
        return ResponseEntity.ok().body(userService.create(userRequest));
    }

    @PostMapping("/login")
    @Operation(summary = "login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok().body(userService.login(loginRequest));
    }

    @GetMapping("/verify-email")
    @Operation(summary = "verify email use in email click link ")
    public RedirectView verifyEmail(@RequestParam String email, @RequestParam String type) {
        return userService.verifyEmail(email, type);
    }

    @PostMapping("/email")
    @Operation(summary = "generate email to verify  again")
    public ResponseEntity<?> generateCode(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.generateLinkVerifyEmail(email, "false", 1, "false"));
    }

    @PostMapping("/forget-password/email")
    @Operation(summary = "generate verify email to reset password")
    public ResponseEntity<?> generateCodeForget(@RequestParam String email) {
        return ResponseEntity.ok().body(userService.generateEmailForgetPassword(email));
    }

    @PutMapping("/forget-password")
    @Operation(summary = "reset password")
    public ResponseEntity<?> resetPassword(@RequestParam String email, @RequestParam String newPassword) {
        return ResponseEntity.ok().body(userService.forgetPassword(email, newPassword));
    }
}
