package account.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class SamePasswordException extends RuntimeException{
    public SamePasswordException() {
        super("The passwords must be different!");
    }

}
