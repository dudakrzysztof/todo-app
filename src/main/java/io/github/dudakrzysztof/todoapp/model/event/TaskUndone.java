package io.github.dudakrzysztof.todoapp.model.event;

import io.github.dudakrzysztof.todoapp.model.Task;

import java.time.Clock;

public class TaskUndone extends TaskEvent {
    TaskUndone(Task source) {
        super(source.getId(), Clock.systemDefaultZone());
    }
}
