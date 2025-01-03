package account.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordValidationException extends RuntimeException {
    public PasswordValidationException() {
        super("Password length must be 12 chars minimum!");
    }
}
