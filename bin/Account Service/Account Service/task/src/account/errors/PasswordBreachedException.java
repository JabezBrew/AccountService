package account.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class PasswordBreachedException extends RuntimeException {
    public PasswordBreachedException() {
        super("The password is in the hacker's database!");
    }
}
