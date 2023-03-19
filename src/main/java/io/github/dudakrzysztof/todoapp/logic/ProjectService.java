package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.TaskConfigurationProperties;
import io.github.dudakrzysztof.todoapp.model.*;
import io.github.dudakrzysztof.todoapp.model.projection.GroupReadModel;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;

    public ProjectService(final ProjectRepository projectRepository, final TaskGroupRepository taskGroupRepository,
                          final TaskConfigurationProperties config) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
    }

    public List<Project> readAll(){
        return projectRepository.findAll();
    }

    public Project save(final Project toSave){
        return projectRepository.save(toSave);
    }

    public GroupReadModel createGroup(int projectId, LocalDateTime deadline){

        if (!config.getTemplate().isAllowMultipleTasks()
                && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId))
            throw new IllegalArgumentException("Only one undone group from project is allowed");

        TaskGroup result = projectRepository.findById(projectId)
                .map(project -> {
                   TaskGroup targetGroup = new TaskGroup();
                   targetGroup.setDescription(project.getDescription());
                   targetGroup.setTasks(
                           project.getSteps().stream()
                                   .map(projectStep -> new Task(
                                           projectStep.getDescription(),
                                           deadline.plusDays(projectStep.getDaysToDeadline()))
                                   ).collect(Collectors.toSet())
                   );
                return targetGroup;
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));

        return new GroupReadModel(result);
    }

}
