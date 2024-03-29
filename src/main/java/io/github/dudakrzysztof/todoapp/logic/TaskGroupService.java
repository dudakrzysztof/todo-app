package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.model.Project;
import io.github.dudakrzysztof.todoapp.model.TaskGroup;
import io.github.dudakrzysztof.todoapp.model.TaskGroupRepository;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import io.github.dudakrzysztof.todoapp.model.projection.GroupReadModel;
import io.github.dudakrzysztof.todoapp.model.projection.GroupWriteModel;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.RequestScope;

import java.util.List;
import java.util.stream.Collectors;

//@Service
//@RequestScope
public class TaskGroupService {

    private TaskGroupRepository repository;
    private TaskRepository taskRepository;

    TaskGroupService(final TaskGroupRepository repository, final TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
        return createGroup(source, null);
    }

    GroupReadModel createGroup(final GroupWriteModel source, Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll()
                .stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public void toggleGroup(int groupId){
        if (taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all the taks first");
        }
        TaskGroup  result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));

        result.setDone(!result.isDone());
        repository.save(result);
    }

}
