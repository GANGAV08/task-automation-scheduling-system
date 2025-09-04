package com.assignment.repository;

import java.time.Instant;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.assignment.entity.Task;
import com.assignment.enums.TaskStatus;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {

	List<Task> findByStatus(TaskStatus status);

	@Query("select t from Task t where t.status = ?1 and t.executionTime <= ?2")
	List<Task> findDue(TaskStatus pending, Instant now);

}
