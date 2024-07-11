package shop.api.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import shop.api.DTO.ExceptionListResponse;
import shop.api.DTO.ExceptionResponse;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.util.*;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handlerNotFoundException(NotFoundException ex, WebRequest req) {

        return new ExceptionResponse( ex.getMessage(),"NOT_FOUND",404);
    }
    @ExceptionHandler(InvalidInputException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse handlerNotFoundException(InvalidInputException ex, WebRequest req) {

        return new ExceptionResponse( ex.getMessage(),"BAD_REQUEST",400);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public ExceptionListResponse handleValidationException(ConstraintViolationException ex) {
        Map<String, String> errorList = new HashMap<>();
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            errorList.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        return new ExceptionListResponse(errorList,"BAD_REQUEST",400);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ExceptionListResponse TodoException(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String,String> errorList = new HashMap<>();
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        for (FieldError fieldError : fieldErrors) {
            String fieldName = fieldError.getField();
            String errorMessage = fieldError.getDefaultMessage();
           errorList.put(fieldName,errorMessage);
        }


        return new ExceptionListResponse(errorList,"BAD_REQUEST",400);
    }

    @ExceptionHandler({ MethodArgumentTypeMismatchException.class })

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse resolveException(MethodArgumentTypeMismatchException ex) {
        String message = "Parameter '" + ex.getParameter().getParameterName() + "' must be '"
                + Objects.requireNonNull(ex.getRequiredType()).getSimpleName() + "'";

        return new ExceptionResponse(message, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value());
    }

    @ExceptionHandler({ HttpRequestMethodNotSupportedException.class })
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)

    public ExceptionResponse resolveException(HttpRequestMethodNotSupportedException ex) {
        String message = "Request method '" + ex.getMethod() + "' not supported. List of all supported methods - "
                + ex.getSupportedHttpMethods();


        return new ExceptionResponse(message, HttpStatus.METHOD_NOT_ALLOWED.getReasonPhrase(),
                HttpStatus.METHOD_NOT_ALLOWED.value());
    }

    @ExceptionHandler({ HttpMessageNotReadableException.class })

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ExceptionResponse resolveException(HttpMessageNotReadableException ex) {
        String message = "Please provide Request Body in valid JSON format";

        return new ExceptionResponse(message, HttpStatus.BAD_REQUEST.getReasonPhrase(),
                HttpStatus.BAD_REQUEST.value());
    }
    // Xử lý tất cả các exception chưa được khai báo
//    @ExceptionHandler(Exception.class)
//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    public ExceptionResponse handlerException(Exception ex, WebRequest req) {
//
//        return new ExceptionResponse( ex.getMessage(),"INTERNAL_SERVER_ERROR",500);
//    }
}