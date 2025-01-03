package account.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class GenericException extends RuntimeException{

    public GenericException(String message) {
        super(message);
    }
}
