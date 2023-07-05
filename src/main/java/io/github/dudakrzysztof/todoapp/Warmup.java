package io.github.dudakrzysztof.todoapp;

import io.github.dudakrzysztof.todoapp.model.Task;
import io.github.dudakrzysztof.todoapp.model.TaskGroup;
import io.github.dudakrzysztof.todoapp.model.TaskGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class Warmup implements ApplicationListener<ContextRefreshedEvent> {
    private static final Logger logger = LoggerFactory.getLogger(Warmup.class);

    private final TaskGroupRepository groupRepository;

    public Warmup(final TaskGroupRepository groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public void onApplicationEvent(final ContextRefreshedEvent event) {
        logger.info("Application warmup after context refreshed");
        final String description = "ApplicationContextEvent";
        if (!groupRepository.existsByDescription(description)){
            logger.info("No required group found! Adding it");
            TaskGroup group = new TaskGroup();
            group.setDescription(description);

            Set<Task> tasks = new HashSet<>();
            tasks.add(new Task("ContextClosedEvent", null, group));
            tasks.add(new Task("ContextRefreshedEvent", null, group));
            tasks.add(new Task("ContextStoppedEvent", null, group));
            tasks.add(new Task("ContextStartedEvent", null, group));

            group.setTasks(tasks);

            groupRepository.save(group);
        }
    }
}
