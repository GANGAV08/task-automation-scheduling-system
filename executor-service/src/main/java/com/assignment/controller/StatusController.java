package com.assignment.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.service.ExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class StatusController {

	private final ExecutionService service;

	public StatusController(ExecutionService service) {
		this.service = service;
	}

	@Operation(summary = "Check async status")
	@GetMapping("/status/{id}")
	public ResponseEntity<Map<String, Object>> status(@PathVariable Long id) {
		log.info("REST request to get Task status by id : {}", id);

		return ResponseEntity.ok(service.getStatus(id));
	}
}
