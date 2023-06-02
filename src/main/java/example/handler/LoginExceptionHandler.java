package example.handler;

import example.handler.exception.InvalidRequestException;
import example.model.dto.response.ListResponse;
import liquibase.pro.packaged.T;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class LoginExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ListResponse<T>> catchInvalidRequestException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ListResponse<>("invalid_request", null , null,
                null, null, null, e.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ListResponse<T>> catchEntityNotFoundException(InvalidRequestException e) {
        log.error(e.getMessage(), e);
        return new ResponseEntity<>(new ListResponse<>("invalid_request", null , null,
                null, null, null, e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}
