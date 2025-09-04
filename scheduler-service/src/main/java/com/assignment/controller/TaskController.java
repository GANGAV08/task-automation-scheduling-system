package com.assignment.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.assignment.dto.CreateTaskRequest;
import com.assignment.dto.Response;
import com.assignment.dto.TaskResponse;
import com.assignment.service.ResponseService;
import com.assignment.service.TaskService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class TaskController {

	private final TaskService taskService;
	private final ResponseService responseService;

	public TaskController(TaskService taskService, ResponseService responseService) {
		this.taskService = taskService;
		this.responseService = responseService;
	}

	@Operation(summary = "Create a task")
	@PostMapping("/tasks")
	public ResponseEntity<Response<TaskResponse>> createTask(@Validated @RequestBody CreateTaskRequest req) {
		log.info("REST request to create Task : {}", req.getName());

		TaskResponse result = taskService.createTask(req);

		return responseService.success("Task created successfully", result);
	}

	@Operation(summary = "List all tasks")
	@GetMapping("/tasks")
	public ResponseEntity<Response<List<TaskResponse>>> getAllTask() {
		log.info("Request to fetch all tasks");

		List<TaskResponse> tasks = taskService.getAllTask();

		return responseService.success("Tasks fetched successfully", tasks);
	}

	@Operation(summary = "Get task by id")
	@GetMapping("/tasks/{id}")
	public ResponseEntity<Response<TaskResponse>> getTaskById(@PathVariable Long id) {
		log.info("Request to get Task with id: {}", id);

		TaskResponse task = taskService.getTaskById(id);

		return responseService.success("Task fetched successfully", task);
	}

	@Operation(summary = "Cancel a task by ID")
	@PostMapping("/tasks/{id}/cancel")
	public ResponseEntity<Response<Object>> cancelTask(@PathVariable Long id) {
		log.info("Request to cancel Task with id: {}", id);

		boolean status = taskService.cancelTask(id);

		if (status) {
			return responseService.success("Task cancelled successfully", null);
		} else {
			return responseService.success("Task already cancelled", null);
		}

	}

}
