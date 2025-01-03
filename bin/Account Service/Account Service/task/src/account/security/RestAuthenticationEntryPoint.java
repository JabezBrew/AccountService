package account.security;

import account.entity.User;
import account.services.EventLogService;
import account.services.UserDetailsServiceImpl;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    UserDetailsServiceImpl userDetailsService;
    EventLogService eventLogService;

    public RestAuthenticationEntryPoint(UserDetailsServiceImpl userDetailsService, EventLogService eventLogService) {
        this.userDetailsService = userDetailsService;
        this.eventLogService = eventLogService;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        try {
            String credentials = request.getHeader("Authorization").split(" ")[1];
            String email = new String(Base64.getDecoder().decode(credentials), StandardCharsets.UTF_8).split(":")[0];

            if (!Objects.equals(authException.getMessage(), "User account is locked")) {
                EventLogService.accessDeniedOrLoginFailedEvent(request.getRequestURI(), "LOGIN_FAILED", email);
            }

            User user = userDetailsService.getByEmail(email);

            if (user != null) {
                if (user.isEnabled() && user.isAccountNonLocked()) {
                    if (user.getFailedAttempt() >= UserDetailsServiceImpl.MAX_FAILED_ATTEMPTS) {
                        eventLogService.bruteForceEvent(request.getRequestURI(), email);
                        userDetailsService.automaticLock(user, email);
                    } else {
                        userDetailsService.increaseFailedAttempts(user);
                    }
                } else if (!user.isAccountNonLocked()) {
                    userDetailsService.unlockWhenTimeExpired(user);
                }

            }

        } catch (Exception e) { // if the user is not found in the database, e is thrown
            e.getSuppressed();
        }
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
    }
}
