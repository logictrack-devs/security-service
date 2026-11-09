package security_service.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    public record ErrorResponse(
            String timestamp,
            int status,
            String error,
            String mensaje
    ) {}

    private ResponseEntity<ErrorResponse> respuesta(
            HttpStatus status, String mensaje) {

        ErrorResponse error = new ErrorResponse(
                Instant.now().toString(),
                status.value(),
                status.getReasonPhrase(),
                mensaje
        );

        return ResponseEntity.status(status).body(error);
    }

    // Usuario o email ya registrado
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> usuarioDuplicado(
            IllegalArgumentException ex) {

        return respuesta(HttpStatus.CONFLICT, ex.getMessage());
    }

    // Usuario o contraseña incorrectos
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponse> credencialesInvalidas(
            AuthenticationException ex) {

        return respuesta(
                HttpStatus.UNAUTHORIZED,
                "Usuario o contraseña incorrectos"
        );
    }

    // Campos inválidos o incompletos
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> validacion(
            MethodArgumentNotValidException ex) {

        String mensaje = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .orElse("Datos inválidos");

        return respuesta(HttpStatus.BAD_REQUEST, mensaje);
    }
}
