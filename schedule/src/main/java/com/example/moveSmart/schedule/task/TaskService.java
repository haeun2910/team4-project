package com.example.moveSmart.schedule.task;

import com.example.moveSmart.schedule.task.dto.TaskDto;
import com.example.moveSmart.schedule.task.entity.Task;
import com.example.moveSmart.schedule.task.repo.TaskRepo;
import com.example.moveSmart.user.AuthenticationFacade;
import com.example.moveSmart.user.entity.UserEntity;
import com.example.moveSmart.user.repo.UserRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskService {
    private final AuthenticationFacade authFacade;
    private final UserRepo userRepo;
    private final TaskRepo taskRepo;

    @Transactional
    public TaskDto createTask(TaskDto taskDto) {
        UserEntity user = authFacade.extractUser();
        UserEntity userId = userRepo.findById(user.getId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        Task task = Task.builder()
                .title(taskDto.getTitle())
                .user(userId)
                .build();

        return TaskDto.fromEntity(taskRepo.save(task), true);
    }

    @Transactional
    public TaskDto updateTask(TaskDto taskDto) {
        UserEntity user = authFacade.extractUser();
        Task existTask = taskRepo.findByUser(user).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        existTask.setTitle(taskDto.getTitle());
        return TaskDto.fromEntity(taskRepo.save(existTask), true);
    }

    @Transactional
    public void deleteTask(Long taskId) {
        UserEntity user = authFacade.extractUser();
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        taskRepo.deleteById(taskId);
    }

    public Page<TaskDto> getTasks(Pageable pageable) {
        UserEntity user = authFacade.extractUser();
        Page<Task> tasks = taskRepo.findByUser(user,pageable);
        return tasks.map(TaskDto::fromEntity);
    }

    public void completeTask(Long taskId) {
        UserEntity user = authFacade.extractUser();
        Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        if (!task.getUser().getId().equals(user.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }
        task.setCompleted(true);
        taskRepo.save(task);
    }
}
