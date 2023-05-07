package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.model.Task;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;


public class TaskService {
    public static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;

    public TaskService(TaskRepository repository) {
        this.repository = repository;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync(){
        logger.info("Supply async");
        return CompletableFuture.supplyAsync(repository::findAll);
    }
}
