package com.assignment.schedulerjobs;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.assignment.dto.PayloadRequest;
import com.assignment.entity.Task;
import com.assignment.enums.TaskStatus;
import com.assignment.repository.TaskRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Component("customTaskScheduler")
@Slf4j
public class TaskScheduler {

	@Autowired
	TaskRepository taskRepository;

	@Autowired
	RestTemplate restTemplate;

	@Autowired
	private ObjectMapper objectMapper;

	@Value("${scheduler.task.max-retries}")
	private int maxRetries;

	@Value("${scheduler.task.retry-delay-ms}")
	private long retryDelayMs;

	@Scheduled(fixedRateString = "${scheduler.pending-tasks.interval}")
	public void processPendingOrders() throws JsonMappingException {
		log.info("Processing pending tasks...");

		List<Task> tasks = taskRepository.findDue(TaskStatus.PENDING, Instant.now());
		log.info("Found {} pending tasks", tasks.size());

		tasks.forEach(task -> {

			// Convert stored JSON to PayloadRequest
			PayloadRequest requestBody;
			try {
				requestBody = objectMapper.readValue(task.getPayloadJson(), PayloadRequest.class);
				requestBody.setTaskId(task.getId());

				log.debug("Posting to webhook for taskId={}, url={}", task.getId(), task.getWebhookUrl());
				Map<String, String> mapResponse = restTemplate.postForObject(task.getWebhookUrl(), requestBody,
						Map.class);
				if (mapResponse != null) {
					Object statusObj = mapResponse.get("status");
					if (statusObj != null) {
						String status = statusObj.toString();
						log.info("Webhook returned status={} for taskId={}", status, task.getId());

						if ("SUCCESS".equalsIgnoreCase(status)) {
							task.setStatus(TaskStatus.SUCCESS);
							handleRecurrence(task);
						} else if ("QUEUED".equalsIgnoreCase(status)) {
							Object checkUrlObj = mapResponse.get("check_url");
							String checkUrl = checkUrlObj.toString();
							log.info("Webhook returned checkUrl={} for taskId={}", checkUrl, task.getId());

							task.setStatus(TaskStatus.RUNNING);
							task.setAsyncCheckUrl(checkUrl);
						} else {
							handleFailedTask(task);
						}
					} else {
						handleFailedTask(task);
					}
					taskRepository.save(task);
					log.debug("Updated taskId={} with status={}", task.getId(), task.getStatus());
				} else {
					handleFailedTask(task);
				}
			} catch (JsonProcessingException e) {
				log.error("Failed to parse payload JSON for taskId={}", task.getId(), e);
				handleFailedTask(task);
				taskRepository.save(task);
			} catch (Exception e) {
				log.error("Unexpected error while processing taskId={}", task.getId(), e);
				handleFailedTask(task);
				taskRepository.save(task);
			}

		});
		log.info("Finished processing {} tasks", tasks.size());
	}

	// for retry logic
	private void handleFailedTask(Task task) {

		if (task.getRetryCount() < maxRetries) {
			task.setRetryCount(task.getRetryCount() + 1);
			task.setNextRetryTime(Instant.now().plusMillis(retryDelayMs));
			task.setStatus(TaskStatus.PENDING); // mark as pending for retry
			log.warn("TaskId={} failed, will retry {}/{}", task.getId(), task.getRetryCount(), maxRetries);
		} else {
			task.setStatus(TaskStatus.FAILED);
			handleRecurrence(task);
			log.error("TaskId={} failed after {} retries, marking as FAILED", task.getId(), maxRetries);
		}
	}

	// recursion logic
	private void handleRecurrence(Task task) {
		switch (task.getRecurrence()) {
		case HOURLY:
			task.setStatus(TaskStatus.PENDING);
			task.setExecutionTime(task.getExecutionTime().plusSeconds(3600)); // 1 hour
			log.info("TaskId={} scheduled to run next HOURLY at {}", task.getId(), task.getExecutionTime());
			break;
		case DAILY:
			task.setStatus(TaskStatus.PENDING);
			task.setExecutionTime(task.getExecutionTime().plusSeconds(86400)); // 1 day
			log.info("TaskId={} scheduled to run next DAILY at {}", task.getId(), task.getExecutionTime());
			break;
		case WEEKLY:
			task.setStatus(TaskStatus.PENDING);
			task.setExecutionTime(task.getExecutionTime().plusSeconds(604800)); // 7 days
			log.info("TaskId={} scheduled to run next WEEKLY at {}", task.getId(), task.getExecutionTime());
			break;
		default:
			// do nothing, task is one-time
			break;
		}
	}

	@Scheduled(fixedRateString = "${scheduler.running-tasks.interval}")
	public void processRunningOrders() {
		log.info("Processing running tasks...");

		List<Task> tasks = taskRepository.findByStatus(TaskStatus.RUNNING);
		tasks.forEach(task -> {
			try {
				// Convert stored JSON to Map
				Map<String, Object> requestBody = objectMapper.readValue(task.getPayloadJson(),
						new TypeReference<Map<String, Object>>() {
						});

				// Call async check endpoint
				Map<String, Object> mapResponse = restTemplate.getForObject(task.getAsyncCheckUrl(), Map.class);

				if (mapResponse != null) {
					Object statusObj = mapResponse.get("status");
					if (statusObj != null) {
						String status = statusObj.toString();
						log.info("TaskId={} | Webhook returned status={}", task.getId(), status);

						if ("SUCCESS".equalsIgnoreCase(status)) {
							task.setStatus(TaskStatus.SUCCESS);
							handleRecurrence(task);
						} else if ("QUEUED".equalsIgnoreCase(status)) {
							task.setStatus(TaskStatus.RUNNING);
						} else {
							handleFailedTask(task);
						}
					} else {
						handleFailedTask(task);
					}
					taskRepository.save(task);
					log.debug("Updated taskId={} with status={}", task.getId(), task.getStatus());
				} else {
					handleFailedTask(task);
				}
			} catch (JsonProcessingException e) {
				log.error("Failed to parse payload JSON for taskId={}", task.getId(), e);
				handleFailedTask(task);
				taskRepository.save(task);
			} catch (Exception ex) {
				log.error("Unexpected error while processing taskId={}", task.getId(), ex);
				handleFailedTask(task);
				taskRepository.save(task);
			}
		});
		log.info("Processed running tasks: {}", tasks.size());
	}

}
