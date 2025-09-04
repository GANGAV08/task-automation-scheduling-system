package com.assignment.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.assignment.dto.Response;

@RestControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler(InvalidPayloadException.class)
	public ResponseEntity<Response<Void>> handleInvalidPayload(InvalidPayloadException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>("Invalid payload format", null));
	}

	@ExceptionHandler(InvalidTaskTimeException.class)
	public ResponseEntity<Response<Void>> handleInvalidTaskTime(InvalidTaskTimeException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ex.getMessage(), null));
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public Object handleNotFound(ResourceNotFoundException ex) {
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Response<>(ex.getMessage(), null));
	}

	@ExceptionHandler(JsonProcessingException.class)
	public ResponseEntity<Response<Void>> handleJsonProcessingException(JsonProcessingException ex) {
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ex.getMessage(), null));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Object> handleInvalidArgument(MethodArgumentNotValidException ex) {
		Map<String, String> errorMap = new HashMap<>();
		ex.getBindingResult().getFieldErrors()
				.forEach(error -> errorMap.put(error.getField(), error.getDefaultMessage()));
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ex.getMessage(), errorMap));
	}

	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<Object> handleInvalidMessage(HttpMessageNotReadableException ex) {
		String errorMessage = ex.getMostSpecificCause() != null ? ex.getMostSpecificCause().getMessage()
				: ex.getMessage();
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response<>(ex.getMessage(), errorMessage));
	}
}
