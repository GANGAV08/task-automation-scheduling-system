package com.assignment.entity;

import java.time.Instant;

import com.assignment.enums.RecurrenceType;
import com.assignment.enums.TaskStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "task")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Task extends AbstractAuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private Instant executionTime;

	@Column(nullable = false, length = 2048)
	private String webhookUrl;

	@Column(columnDefinition = "text")
	private String payloadJson;

	@Enumerated(EnumType.STRING)
	private RecurrenceType recurrence = RecurrenceType.NONE;

	@Enumerated(EnumType.STRING)
	private TaskStatus status;

	private String asyncCheckUrl;

	private int retryCount = 0;

	private Instant nextRetryTime;

}
