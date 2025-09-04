package com.assignment.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.dto.PayloadRequest;
import com.assignment.entity.ExecutionRecord;
import com.assignment.service.ExecutionService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class WebhookController {

	private final ExecutionService service;

	public WebhookController(ExecutionService service) {
		this.service = service;
	}

	@Value("${baseUrl}")
	String baseUrl;

	@Operation(summary = "Send Welcome Email (sync)")
	@PostMapping("/send-welcome")
	public ResponseEntity<Map<String, Object>> sendWelcome(@RequestBody PayloadRequest body) {
		log.info("REST request to send welcome email for: {}", body.getEmail());

		Map<String, Object> res = service.syncProcess("send-welcome", body);
		return ResponseEntity.accepted().body(res);
	}

	@Operation(summary = "Notify Admin on New Signup (sync)")
	@PostMapping("/notify-admin")
	public ResponseEntity<Map<String, Object>> notifyAdmin(@RequestBody PayloadRequest body) {
		log.info("REST request to notify admin on new signup for: {}", body.getEmail());

		Map<String, Object> res = service.syncProcess("notify-admin", body);
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "Daily Summary (async)")
	@PostMapping("/daily-summary")
	public ResponseEntity<Map<String, Object>> dailySummary(@RequestBody PayloadRequest body) {
		log.info("REST request to send daily summary for: {}", body.getEmail());

		Map<String, Object> res = service.asyncProcess("daily-summary", body, baseUrl);
		return ResponseEntity.accepted().body(res);
	}

	@Operation(summary = "Security Alert (async)")
	@PostMapping("/security-alert")
	public ResponseEntity<Map<String, Object>> securityAlert(@RequestBody PayloadRequest body) {
		log.info("REST request to send security alert for: {}", body.getEmail());

		Map<String, Object> res = service.asyncProcess("security-alert", body, baseUrl);
		return ResponseEntity.ok(res);
	}

	@Operation(summary = "Check task Execution history")
	@GetMapping("/execution-history/{taskId}")
	public ResponseEntity<List<ExecutionRecord>> taskExecutionHistory(@PathVariable Long taskId) {
		log.info("REST request to get Task history by taskId : {}", taskId);
		List<ExecutionRecord> result = service.findByTaskId(taskId);
		return ResponseEntity.ok(result);
	}

}
