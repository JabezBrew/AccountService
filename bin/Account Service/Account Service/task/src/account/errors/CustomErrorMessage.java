package account.errors;


import java.time.LocalDateTime;

public record CustomErrorMessage(LocalDateTime timestamp,
                                 int statusCode,
                                 String error,
                                 String message,
                                 String path) {}
