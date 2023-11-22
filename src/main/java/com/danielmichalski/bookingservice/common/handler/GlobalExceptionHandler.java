package com.danielmichalski.bookingservice.common.handler;

import com.danielmichalski.bookingservice.common.exception.NotFoundException;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

  @ResponseBody
  @ExceptionHandler(value = {Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorDTO handleException(Exception exception) {
    log.error(exception.getMessage(), exception);
    return ErrorDTO.builder()
        .code(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
        .message("Unexpected error!")
        .build();
  }

  @ResponseBody
  @ExceptionHandler(value = NotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorDTO handleException(NotFoundException notFoundException) {
    log.error(notFoundException.getMessage(), notFoundException);
    return ErrorDTO.builder()
        .code(HttpStatus.NOT_FOUND.getReasonPhrase())
        .message(notFoundException.getMessage())
        .build();
  }

  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(error -> {
          String fieldName = ((FieldError) error).getField();
          String errorMessage = error.getDefaultMessage();
          errors.put(fieldName, errorMessage);
        });
    return ResponseEntity.badRequest().body(errors);
  }

}
