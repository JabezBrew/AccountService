package account.errors;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.io.IOException;
import java.time.LocalDateTime;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(MoneyAlreadyAllocatedException.class)
    public ResponseEntity<CustomErrorMessage> moneyAlreadyAllocatedException(MoneyAlreadyAllocatedException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<CustomErrorMessage> notFoundException(NotFoundException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_NOT_FOUND,
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<CustomErrorMessage> userAlreadyExistsException(UserAlreadyExistsException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SamePasswordException.class)
    public ResponseEntity<CustomErrorMessage> samePasswordException(SamePasswordException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordValidationException.class)
    public ResponseEntity<CustomErrorMessage> passwordValidationException(PasswordValidationException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PasswordBreachedException.class)
    public ResponseEntity<CustomErrorMessage> passwordBreachedException(PasswordBreachedException e, WebRequest request) throws IOException {
        CustomErrorMessage customErrorMessage = new CustomErrorMessage(LocalDateTime.now(),
                HttpServletResponse.SC_BAD_REQUEST,
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                e.getMessage(),
                request.toString().substring(request.toString().indexOf("/"), request.toString().indexOf(";")));
        return new ResponseEntity<>(customErrorMessage, HttpStatus.BAD_REQUEST);
    }
}
