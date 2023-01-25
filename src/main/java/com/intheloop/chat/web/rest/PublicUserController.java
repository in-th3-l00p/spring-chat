package com.intheloop.chat.web.rest;

import com.intheloop.chat.domain.User;
import com.intheloop.chat.service.UserService;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/user")
public class PublicUserController {
    private final UserService userService;

    public PublicUserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterBody registerBody) {
        User user = new User();
        user.setUsername(registerBody.getUsername());
        user.setPassword(registerBody.getPassword());
        System.out.println(user.getUsername() + " " + user.getPassword());
        userService.createUser(user);
        return ResponseEntity.ok("Created");
    }

    @Getter
    @Setter
    @NoArgsConstructor
    private static class RegisterBody {
        private String username;
        private String password;
    }
}
