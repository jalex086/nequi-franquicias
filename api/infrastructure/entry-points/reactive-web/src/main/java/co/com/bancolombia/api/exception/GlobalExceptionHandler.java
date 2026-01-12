package co.com.bancolombia.api.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationErrors(WebExchangeBindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Validation Failed")
                .message("Invalid input parameters")
                .details(errors)
                .build();
                
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
    
    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleRuntimeException(RuntimeException ex) {
        // Manejar errores "not found" como 404
        if (ex.getMessage() != null && ex.getMessage().contains("not found")) {
            log.warn("Resource not found: {}", ex.getMessage());
            
            ErrorResponse errorResponse = ErrorResponse.builder()
                    .timestamp(LocalDateTime.now())
                    .status(HttpStatus.NOT_FOUND.value())
                    .error("Not Found")
                    .message(ex.getMessage())
                    .build();
                    
            return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
        }
        
        log.error("Runtime exception occurred", ex);
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("Internal Server Error")
                .message("An unexpected error occurred")
                .build();
                
        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
    }
    
    @ExceptionHandler(IllegalArgumentException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        
        ErrorResponse errorResponse = ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error("Bad Request")
                .message(ex.getMessage())
                .build();
                
        return Mono.just(ResponseEntity.badRequest().body(errorResponse));
    }
}
