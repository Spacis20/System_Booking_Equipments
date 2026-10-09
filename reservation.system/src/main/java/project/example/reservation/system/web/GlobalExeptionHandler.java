package project.example.reservation.system.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;


@ControllerAdvice
public class GlobalExeptionHandler {

    private Logger log = LoggerFactory.getLogger(GlobalExeptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorExeptionDTO> handleAllExeption(Exception e){
        log.error("Handle exeption e={}", e);

        var errorDto = new ErrorExeptionDTO(
                "Internal server error",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    @ExceptionHandler(exception = {
     IllegalArgumentException.class,
     IllegalStateException.class,
     MethodArgumentNotValidException.class
    }
    )
    public ResponseEntity<ErrorExeptionDTO> handleBadRequest(Exception e){
        log.error("Handle Bad Request exeption e={}", e);

        var errorDto = new ErrorExeptionDTO(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorExeptionDTO> handleNotFound(EntityNotFoundException e){
        log.error("Not found entity e={}", e);

        var errorDto = new ErrorExeptionDTO(
                "Entity Not Found",
                e.getMessage(),
                LocalDateTime.now()
        );

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

}
