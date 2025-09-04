package com.assignment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.assignment.entity.ExecutionRecord;

@Repository
public interface ExecutionRecordRepository extends JpaRepository<ExecutionRecord, Long> {

	List<ExecutionRecord> findByTaskId(Long taskId);

}
