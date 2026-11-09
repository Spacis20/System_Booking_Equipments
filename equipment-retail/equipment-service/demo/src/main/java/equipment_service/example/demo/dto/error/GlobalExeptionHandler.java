package equipment_service.example.demo.dto.error;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
@RestControllerAdvice
public class GlobalExeptionHandler {

    @ExceptionHandler(exception = Exception.class)
    public ResponseEntity<ErrorExeptionDto> handleAllExeption(Exception e){
        log.info("Handle exeption = {}", e);

        var errorDto = new ErrorExeptionDto(
            "Internal Server Error",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorDto);
    }

    @ExceptionHandler(exception = {
            IllegalArgumentException.class,
            IllegalStateException.class,
            MethodArgumentNotValidException.class
    })
    public ResponseEntity<ErrorExeptionDto> hanldeIllegalException(Exception e){
        log.info("Handle BADREQUEST exeption: {}", e);

        var errorDto = new ErrorExeptionDto(
                "Bad request",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorDto);
    }

    @ExceptionHandler(exception = EntityNotFoundException.class)
    public ResponseEntity<ErrorExeptionDto> handleNotFoundException(Exception e){
        log.info("Entity not found: {}", e);
        var errorDto = new ErrorExeptionDto(
                "Not found entity",
                e.getMessage(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDto);
    }

}
