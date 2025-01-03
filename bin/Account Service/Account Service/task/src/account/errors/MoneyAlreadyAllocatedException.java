package account.errors;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class MoneyAlreadyAllocatedException extends RuntimeException{
public MoneyAlreadyAllocatedException() {
        super("Money already allocated");
    }
}
