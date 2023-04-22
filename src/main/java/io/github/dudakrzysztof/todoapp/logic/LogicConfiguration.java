package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.TaskConfigurationProperties;
import io.github.dudakrzysztof.todoapp.model.ProjectRepository;
import io.github.dudakrzysztof.todoapp.model.TaskGroupRepository;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(final ProjectRepository repository,
                                  final TaskGroupRepository taskGroupRepository,
                                  final TaskConfigurationProperties configuration,
                                  final TaskGroupService taskGroupService
    ) {
        return new ProjectService(repository, taskGroupRepository, configuration, taskGroupService);
    }

    @Bean
//    @RequestScope
    TaskGroupService taskGroupService (final TaskGroupRepository taskGroupRepository,
                                       final TaskRepository taskRepository){
        return new TaskGroupService(taskGroupRepository, taskRepository);
    }
}
