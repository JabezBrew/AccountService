package account.controllers;

import account.entity.User;
import account.dto.UserDTO;
import jakarta.validation.Valid;

import account.repo.UserRepository;
import account.services.UserDetailsServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    UserRepository userRepo;
    UserDetailsServiceImpl userDetailsService;
    @Autowired
    public AuthController(UserRepository userRepo, UserDetailsServiceImpl userDetailsService) {
        this.userRepo = userRepo;
        this.userDetailsService = userDetailsService;

    }

    @PostMapping("/signup")
    public UserDTO signUp(@Valid @RequestBody User user) {
        userDetailsService.saveUser(user);
        return UserDTO.mapToDTO(user);
    }

    @PostMapping("/changepass")
    public Map<String, String> changePassword(@RequestBody Map<String, String> newPassWord) {
       userDetailsService.changepass(newPassWord.get("new_password"));
       return Map.of("email", UserDetailsServiceImpl.getCurrentUser().getEmail(), "status", "The password has been updated successfully");
    }

}
