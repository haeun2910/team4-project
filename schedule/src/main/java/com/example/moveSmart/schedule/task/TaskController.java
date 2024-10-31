package com.example.moveSmart.schedule.task;

import com.example.moveSmart.schedule.task.dto.TaskDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;

    @PostMapping("create")
    public TaskDto create(@RequestBody TaskDto taskDto) {
        return taskService.createTask(taskDto);
    }
    @PutMapping("update/{taskId}")
    public TaskDto update(@RequestBody TaskDto taskDto, @PathVariable("taskId") Long taskId) {
        return taskService.updateTask(taskId, taskDto);
    }
    @DeleteMapping("delete/{taskId}")
    public void delete(@PathVariable("taskId") Long taskId) {
        taskService.deleteTask(taskId);
    }
    @GetMapping("my-tasks")
    public Page<TaskDto> getMyTasks(Pageable pageable) {
        return taskService.getTasks(pageable);
    }
    @PutMapping("complete/{taskId}")
    public ResponseEntity<String> completeTask(@PathVariable("taskId") Long id) {
        taskService.completeTask(id);
        return ResponseEntity.ok("task marked as completed");
    }
}
