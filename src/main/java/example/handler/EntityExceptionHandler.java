package example.handler;

import example.handler.exception.EntityNotFoundException;
import example.handler.exception.InvalidRequestException;
import example.handler.exception.UnableCreateEntityException;
import example.handler.exception.UnableUpdateEntityException;
import example.model.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class EntityExceptionHandler {
    private final static String INVALID_REQUEST = "invalid_request";
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchInvalidRequestException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
                return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchEntityNotFoundException(EntityNotFoundException e){
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnableCreateEntityException(UnableCreateEntityException e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> catchUnableUpdateEntityException(UnableUpdateEntityException e) {
        log.error(e.getMessage(), e);
        ErrorResponse errorResponse = ErrorResponse.builder()
                .error(INVALID_REQUEST)
                .errorDescription(e.getMessage())
                .build();
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }
}
