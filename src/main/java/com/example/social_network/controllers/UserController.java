package com.example.social_network.controllers;

import com.example.social_network.dto.LoginDto;
import com.example.social_network.dto.UserRegistrationDto;
import com.example.social_network.entity.User;
import com.example.social_network.repositories.UserRepository;
import com.example.social_network.services.JwtTokenService;
import com.example.social_network.services.UserService;
import com.example.social_network.utils.CustomUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenService jwtTokenService;
    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserRegistrationDto registrationDto) {
        User registeredUser = userService.registerUser(registrationDto);
        String token = jwtTokenService.createToken(registeredUser.getUsername());

        Map<String, Object> response = new HashMap<>();
        response.put("user", registeredUser);
        response.put("token", token);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginDto loginDto) {
        Authentication authentication = authenticateUser(loginDto.getUsername(), loginDto.getPassword());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtTokenService.createToken(loginDto.getUsername());
        CustomUser loggedInUser = (CustomUser) authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();
        response.put("user", loggedInUser);
        response.put("token", token);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElse(null);
        if (user != null) {
            user.setIsOnline(false);
            user.setLastSeen(LocalDateTime.now());
            userRepository.save(user);
            return ResponseEntity.ok("successful logout");
        }
        return ResponseEntity.ok("user not found");
    }

    @PostMapping("/validateToken")
    public ResponseEntity<?> validateToken(@RequestBody String token) {
        System.out.println("token=" + token);
        try {
            if (jwtTokenService.validateToken(token))
                return ResponseEntity.ok().build();
            else
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    private Authentication authenticateUser(String username, String password) {
        return authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
    }


    @PutMapping("/{userId}/change-password")
    public ResponseEntity<String> changePassword(@PathVariable Long userId,
                                                 @RequestParam String oldPassword,
                                                 @RequestParam String newPassword) {
        userService.changePassword(userId, oldPassword, newPassword);
        return ResponseEntity.ok("Password updated successfully");
    }

    @PutMapping("/{userId}/change-username")
    public ResponseEntity<String> changeUsername(@PathVariable Long userId,
                                                 @RequestParam String newUsername) {
        userService.changeUsername(userId, newUsername);
        return ResponseEntity.ok("Username updated successfully");
    }

    @PutMapping("/{userId}/change-email")
    public ResponseEntity<String> changeEmail(@PathVariable Long userId,
                                              @RequestParam String newEmail) {
        userService.changeEmail(userId, newEmail);
        return ResponseEntity.ok("Email updated successfully");
    }

    @PutMapping("/{userId}/deactivate")
    public ResponseEntity<String> deactivateUser(
            @PathVariable Long userId,
            @RequestHeader("Authorization") String authorizationHeader // Получаем токен из заголовка
    ) {
        // Извлекаем токен из заголовка
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("Invalid or missing Authorization header");
        }

        String token = authorizationHeader.substring(7); // Убираем "Bearer "

        userService.deactivateUser(userId, token);

        return ResponseEntity.ok("User deactivated successfully");
    }

    @PutMapping("/{userId}/restore")
    public ResponseEntity<String> restoreUser(@PathVariable Long userId) {
        userService.restoreUser(userId);
        return ResponseEntity.ok("User restored successfully");
    }
}
