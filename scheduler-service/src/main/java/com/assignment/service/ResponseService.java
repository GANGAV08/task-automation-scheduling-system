package com.assignment.service;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.assignment.dto.Response;

@Service
public class ResponseService {

	/**
	 * Success response with data
	 */
	public <T> ResponseEntity<Response<T>> success(String message, T data) {
		Response<T> response = new Response<>(message, data);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

	/**
	 * Failure response with only message
	 */
	public ResponseEntity<Response<Void>> failure(HttpStatus status, String message) {
		return ResponseEntity.status(status).body(new Response<>(message, null));
	}

	/**
	 * Failure response with extra data
	 */
	public <T> ResponseEntity<Response<T>> exceptionFailure(HttpStatus status, String message, T data) {
		Response<T> response = new Response<>(message, data);
		return ResponseEntity.status(status).body(response);
	}
}
