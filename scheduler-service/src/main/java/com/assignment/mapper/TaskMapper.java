package com.assignment.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.assignment.dto.TaskResponse;
import com.assignment.entity.Task;

@Mapper(componentModel = "spring")
public interface TaskMapper {

	@Mapping(target = "id", source = "id")
	@Mapping(target = "status", source = "status")
	@Mapping(target = "asyncCheckUrl", source = "asyncCheckUrl")

	TaskResponse taskToTaskResponse(Task task);

	Task taskResponseToTask(TaskResponse taskResponse);

	List<TaskResponse> taskListToTaskResponseList(List<Task> taskList);
}
