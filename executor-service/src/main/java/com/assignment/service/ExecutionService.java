package com.assignment.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.dto.PayloadRequest;
import com.assignment.entity.ExecutionRecord;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.repository.ExecutionRecordRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ExecutionService {

	private final ExecutionRecordRepository repo;

	private final ObjectMapper mapper = new ObjectMapper();

	private final TaskScheduler scheduler;

	private final MailService mailService;

	@Value("${adminMailForNotify}")
	String adminMail;

	public ExecutionService(ExecutionRecordRepository repo, MailService mailService) {
		this.repo = repo;
		this.mailService = mailService;
		ThreadPoolTaskScheduler ts = new ThreadPoolTaskScheduler();
		ts.setPoolSize(4);
		ts.initialize();
		this.scheduler = ts;
	}

	@Transactional
	public Map<String, Object> syncProcess(String kind, PayloadRequest payload) {
		try {
			String json = mapper.writeValueAsString(payload);
			ExecutionRecord rec = new ExecutionRecord();
			rec.setKind(kind);
			rec.setTaskId(payload.getTaskId());
			rec.setPayloadJson(json);

			// Only send email if kind is "send-welcome"
			if ("send-welcome".equals(kind)) {
				mailService.sendWelcomeEmail(payload); // FreeMarker template
			}

			// Only send email if kind is "send-welcome"
			if ("notify-admin".equals(kind)) {
				mailService.sendAdminNotification(adminMail, payload); // FreeMarker template
			}

			rec.setStatus("SUCCESS");
			rec = repo.save(rec);
			log.info("Sync process completed for kind={} with id={}", kind, rec.getId());

			return Map.of("status", "SUCCESS", "id", rec.getId().toString());
		} catch (Exception e) {
			log.error("Sync process failed for kind={}", kind, e);
			return Map.of("status", "FAILED");
		}
	}

	@Transactional
	public Map<String, Object> asyncProcess(String kind, PayloadRequest payload, String baseUrl) {
		try {
			String json = mapper.writeValueAsString(payload);
			ExecutionRecord rec = new ExecutionRecord();
			rec.setKind(kind);
			rec.setPayloadJson(json);
			rec.setTaskId(payload.getTaskId());
			rec.setStatus("QUEUED");
			rec = repo.save(rec);
			Long id = rec.getId();

			// simulate work after some delay, with potential success/failure
			long delay = ThreadLocalRandom.current().nextLong(3, 20);
			scheduler.schedule(() -> finishAsync(id), java.util.Date.from(java.time.Instant.now().plusSeconds(delay)));

			log.info("Async process queued for kind={} with id={} (delay={}s)", kind, id, delay);

			return Map.of("status", "QUEUED", "check_url", baseUrl + "/api/status/" + id);
		} catch (Exception e) {
			log.error("Async process failed for kind={}", kind, e);
			return Map.of("status", "FAILED");
		}
	}

	@Transactional
	public Map<String, Object> getStatus(Long id) {
		var rec = repo.findById(id).orElse(null);
		if (rec == null)
			return Map.of("status", "NOT_FOUND");
		return Map.of("status", rec.getStatus(), "id", rec.getId().toString());
	}

	@Transactional
	public void finishAsync(Long id) {
		var rec = repo.findById(id).orElse(null);
		rec.setStatus(ThreadLocalRandom.current().nextInt(100) < 90 ? "SUCCESS" : "FAILED");
		repo.save(rec);
	}

	public List<ExecutionRecord> findByTaskId(Long taskId) {
		List<ExecutionRecord> result = repo.findByTaskId(taskId);
		if (result == null) {
			throw new ResourceNotFoundException("record not found");
		}
		return result;
	}
}
