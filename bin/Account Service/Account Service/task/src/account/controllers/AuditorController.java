package account.controllers;

import account.entity.EventLog;
import account.services.UserDetailsServiceImpl;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AuditorController {

    UserDetailsServiceImpl userDetailsService;

    public AuditorController(UserDetailsServiceImpl userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @GetMapping("/security/events")
    public List<EventLog> getSecurityEvents() {
        return userDetailsService.getEventLogs();
    }
}
