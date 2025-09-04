package com.assignment.dto;

import java.time.Instant;

import com.assignment.enums.RecurrenceType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateTaskRequest {

	@NotBlank
	public String name;

	@NotNull
	public Instant executionTime;

	@NotBlank
	public String webhookUrl;

	public Object payload;

	@NotNull
	public RecurrenceType recurrence = RecurrenceType.NONE;

}