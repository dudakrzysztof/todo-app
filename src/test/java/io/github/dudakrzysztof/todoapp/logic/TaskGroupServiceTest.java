package io.github.dudakrzysztof.todoapp.logic;


import io.github.dudakrzysztof.todoapp.model.TaskGroup;
import io.github.dudakrzysztof.todoapp.model.TaskGroupRepository;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TaskGroupServiceTest {
    @Test
    @DisplayName("should throw IllegaStateException when group has undone tasks")
    void toggleGroup_undoneTasks_throwsIllegalStateException(){
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(true);
        // system under test
        TaskGroupService toTest = new TaskGroupService(null, mockTaskRepository);
        // when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("has undone tasks");
    }

    @Test
    @DisplayName("should throw IllegaStateException when given id not found")
    void toggleGroup_idNotFound_throwsIllegalArgumentException(){
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        TaskGroupRepository mockTaskGroupRepository = groupRepositoryReturning();
        // system under test
        TaskGroupService toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        // when
        Throwable exception = catchThrowable(() -> toTest.toggleGroup(1));
        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should toggle group")
    void toggleGroup_allTasksAreDone_setDoneToGroup(){
        // given
        TaskRepository mockTaskRepository = taskRepositoryReturning(false);
        // and
        TaskGroup group = new TaskGroup();
        boolean beforeToggle = group.isDone();
        //and
        TaskGroupRepository mockTaskGroupRepository = mock(TaskGroupRepository.class);
        when(mockTaskGroupRepository.findById(anyInt())).thenReturn(Optional.of(group));

        // system under test
        TaskGroupService toTest = new TaskGroupService(mockTaskGroupRepository, mockTaskRepository);
        // when
        toTest.toggleGroup(1);

        //then
        assertThat(group.isDone()).isNotEqualTo(beforeToggle);
    }


    private TaskGroupRepository groupRepositoryReturning() {
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.findById(anyInt())).thenReturn(Optional.empty());
        return mockGroupRepository;
    }

    private TaskRepository taskRepositoryReturning(final boolean result){
        TaskRepository mockTaskRepository = mock(TaskRepository.class);
        when(mockTaskRepository.existsByDoneIsFalseAndGroup_Id(anyInt())).thenReturn(result);
        return mockTaskRepository;
    }
}