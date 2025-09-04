package com.assignment.service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.assignment.dto.CreateTaskRequest;
import com.assignment.dto.TaskResponse;
import com.assignment.entity.Task;
import com.assignment.enums.TaskStatus;
import com.assignment.exception.InvalidPayloadException;
import com.assignment.exception.InvalidTaskTimeException;
import com.assignment.exception.ResourceNotFoundException;
import com.assignment.mapper.TaskMapper;
import com.assignment.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskService {

	private final TaskRepository taskRepo;
	private final ObjectMapper objectMapper;

	private final TaskMapper taskMapper;

	public TaskService(TaskRepository taskRepo, ObjectMapper objectMapper, TaskMapper taskMapper) {
		this.taskRepo = taskRepo;
		this.objectMapper = objectMapper;
		this.taskMapper = taskMapper;
	}

	@Transactional
	public TaskResponse createTask(CreateTaskRequest req) {
		log.info("Creating task: {}", req.getName());
		Instant now = Instant.now();

		// Log the execution time along with the current time
		log.info("ExecutionTime: {}  CurrentTime: {}", req.getExecutionTime(), now);

		if (req.getExecutionTime() != null && req.getExecutionTime().isBefore(now)) {
			throw new InvalidTaskTimeException("Execution time cannot be in the past");
		}

		Task task = new Task();
		task.setName(req.getName());
		task.setExecutionTime(req.getExecutionTime());
		task.setWebhookUrl(req.getWebhookUrl());

		try {
			task.setPayloadJson(req.payload == null ? null : objectMapper.writeValueAsString(req.payload));
		} catch (Exception ex) {
			throw new InvalidPayloadException("Invalid payload JSON", ex);
		}

		task.setRecurrence(req.getRecurrence());
		task.setStatus(TaskStatus.PENDING);
		task = taskRepo.save(task);
		return taskMapper.taskToTaskResponse(task);
	}

	public List<TaskResponse> getAllTask() {
		List<Task> list = taskRepo.findAll();
		if (list == null) {
			throw new ResourceNotFoundException("Tasks not found");
		}
		return taskMapper.taskListToTaskResponseList(list);
	}

	public TaskResponse getTaskById(Long id) {
		Optional<Task> task = taskRepo.findById(id);
		if (!task.isPresent()) {
			throw new ResourceNotFoundException("Task not found with id: " + id);
		}
		return taskMapper.taskToTaskResponse(task.get());
	}

	@Transactional
	public boolean cancelTask(Long taskId) {
		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("Task not found with id: " + taskId));

		if (task.getStatus() != TaskStatus.CANCELLED) {
			task.setStatus(TaskStatus.CANCELLED);
			taskRepo.save(task);
			log.info("Task with id={} has been cancelled", taskId);
			return true;
		} else {
			log.warn("Task with id={} is already cancelled", taskId);
			return false;
		}
	}

}