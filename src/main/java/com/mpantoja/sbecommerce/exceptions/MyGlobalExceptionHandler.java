package com.mpantoja.sbecommerce.exceptions;

import com.mpantoja.sbecommerce.payload.APIResponse;
import jakarta.persistence.ElementCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static java.lang.System.err;

@RestControllerAdvice
public class MyGlobalExceptionHandler {

    //Method for Validation exceptions
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException e) {

        Map<String, String> response = new HashMap<>();
        e.getBindingResult().getAllErrors().forEach(err->{
            String fieldName = ((FieldError) err).getField();
            String message = (err).getDefaultMessage();
            response.put(fieldName, message);
        });
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> myResourceNotFoundException(ResourceNotFoundException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> myApiException(APIException e){
        String message = e.getMessage();
        APIResponse apiResponse = new APIResponse(message, false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);

    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> myMessageNotReadableException(HttpMessageNotReadableException e){

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error in Request body",e.getMessage(),
                "Exception",e.getClass().getSimpleName()));
    }



}
