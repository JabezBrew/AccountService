package account.controllers;

import account.dto.UserDTO;
import account.services.UserDetailsServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/user")
public class AdminController {
    UserDetailsServiceImpl userDetailsService;
    public AdminController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @GetMapping("/")
    public List<UserDTO> getUsers() {
        return userDetailsService.getAllUsers();
    }
    @DeleteMapping("/{user_email}")
    public LinkedHashMap<String, String> deleteUser(@PathVariable(required = false) String user_email) {
        userDetailsService.deleteUser(user_email);

        return new LinkedHashMap<>() {{
            put("user", user_email);
            put("status", "Deleted successfully!");
        }};
    }
    @PutMapping("/role")
    public UserDTO changeRole(@RequestBody Map<String, String> roleChange) {
        return userDetailsService.changeRole(roleChange);
    }

    @PutMapping("/access")
    public Map<String, String> changeAccess(@RequestBody Map<String, String> accessChange) {
        userDetailsService.changeUserAccess(accessChange);

        return Map.of("status", "User "+accessChange.get("user").toLowerCase()+" "+accessChange.get("operation").toLowerCase()+"ed!") ;
    }


}
