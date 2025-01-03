package account.errors;

import account.services.EventLogService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.bind.annotation.ControllerAdvice;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@ControllerAdvice
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest request,
            HttpServletResponse response,
            AccessDeniedException exc) throws IOException {



        String credentials = request.getHeader("Authorization").split(" ")[1];
        String email = new String(Base64.getDecoder().decode(credentials), StandardCharsets.UTF_8).split(":")[0];

        System.out.println(request.getRequestURI()+ " "+ email);
        response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied!");
        EventLogService.accessDeniedOrLoginFailedEvent(request.getRequestURI(), "ACCESS_DENIED", email);

    }
}
