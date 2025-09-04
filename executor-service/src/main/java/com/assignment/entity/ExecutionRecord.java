package com.assignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "executions")
public class ExecutionRecord extends AbstractAuditingEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private Long taskId;

	private String name;

	private String kind;

	@Column(columnDefinition = "text")
	private String payloadJson;

	private String status; // QUEUED, RUNNING, SUCCESS, FAILED
}
