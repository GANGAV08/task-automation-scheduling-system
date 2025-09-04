package com.assignment.dto;

import java.time.Instant;

import com.assignment.enums.RecurrenceType;
import com.assignment.enums.TaskStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

	public Long id;

	public String name;

	public Instant executionTime;

	public String webhookUrl;

	public String payload;

	public RecurrenceType recurrence;

	public TaskStatus status;

	private String asyncCheckUrl;

}
