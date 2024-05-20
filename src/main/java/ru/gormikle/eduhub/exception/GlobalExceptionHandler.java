package ru.gormikle.eduhub.exception;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.jcraft.jsch.JSchException;

import java.util.concurrent.CompletionException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(CompletionException.class)
    public ResponseEntity<String> handleCompletionException(CompletionException e) {
        Throwable cause = e.getCause();
        if (cause instanceof JSchException) {
            return new ResponseEntity<>("Unauthorized access: " + cause.getMessage(), HttpStatus.UNAUTHORIZED);
        } else {
            return new ResponseEntity<>("Internal server error: " + cause.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleException(Exception e) {
        return new ResponseEntity<>("Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

