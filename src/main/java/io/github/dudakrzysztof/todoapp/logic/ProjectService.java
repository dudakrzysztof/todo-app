package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.TaskConfigurationProperties;
import io.github.dudakrzysztof.todoapp.model.*;
import io.github.dudakrzysztof.todoapp.model.projection.GroupReadModel;
import io.github.dudakrzysztof.todoapp.model.projection.GroupTaskWriteModel;
import io.github.dudakrzysztof.todoapp.model.projection.GroupWriteModel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

//@Service
public class ProjectService {
    private ProjectRepository projectRepository;
    private TaskGroupRepository taskGroupRepository;
    private TaskConfigurationProperties config;
    private TaskGroupService taskGroupService;

    public ProjectService(final ProjectRepository projectRepository, final TaskGroupRepository taskGroupRepository,
                          final TaskConfigurationProperties config, final TaskGroupService taskGroupService) {
        this.projectRepository = projectRepository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.taskGroupService = taskGroupService;
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
            throw new IllegalStateException("Only one undone group from project is allowed");

        return projectRepository.findById(projectId)
                .map(project -> {
                    GroupWriteModel targetGroup = new GroupWriteModel();
                   targetGroup.setDescription(project.getDescription());
                   targetGroup.setTasks(
                           project.getSteps().stream()
                                   .map(projectStep -> {
                                       GroupTaskWriteModel task = new GroupTaskWriteModel();
                                       task.setDescription(projectStep.getDescription());
                                       task.setDeadline(deadline.plusDays(projectStep.getDaysToDeadline()));
                                       return task;
                                   }
                                   ).collect(Collectors.toSet())
                   );
                     return taskGroupService.createGroup(targetGroup);

                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }

}
