package io.github.dudakrzysztof.todoapp.controller;


import io.github.dudakrzysztof.todoapp.logic.TaskGroupService;
import io.github.dudakrzysztof.todoapp.model.Task;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import io.github.dudakrzysztof.todoapp.model.projection.GroupReadModel;
import io.github.dudakrzysztof.todoapp.model.projection.GroupWriteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/groups")
public class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);

    private final TaskRepository taskRepository;
    private final  TaskGroupService taskGroupService;

    public TaskGroupController(final TaskRepository taskRepository, final TaskGroupService taskGroupService) {

        this.taskRepository = taskRepository;
        this.taskGroupService = taskGroupService;
    }

    @PostMapping
    ResponseEntity<GroupReadModel> createTaskGroup(@RequestBody @Valid GroupWriteModel toCreate){
        GroupReadModel result = taskGroupService.createGroup(toCreate);
        return ResponseEntity.created(URI.create("/" + result.getId()))
                .body(result);
    }

    @GetMapping
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        logger.warn("Exposing all the groups");
        return ResponseEntity.ok(taskGroupService.readAll());
    }

    @GetMapping("/{id}/tasks")
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id) {
        logger.warn("Exposing all tasks in the group with specific Id");
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }


    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id){
        taskGroupService.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<?> handleIllegalArgument(IllegalArgumentException e){
        return ResponseEntity.notFound().build();
    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<String> handleIllegalState(IllegalStateException e){
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
