package io.github.dudakrzysztof.todoapp.logic;

import io.github.dudakrzysztof.todoapp.TaskConfigurationProperties;
import io.github.dudakrzysztof.todoapp.model.*;
import io.github.dudakrzysztof.todoapp.model.projection.GroupReadModel;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ProjectServiceTest {

    @Test
    @DisplayName("should throw IllegalStateException when configured to allow just 1 group and the other undone group exists")
    void createGroup_noMultipleGroupsConfig_And_undoneGroupExists_throwsIllegalStateException() {
        //given
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(true);
        //and
        TaskConfigurationProperties mockConfig = configurationReturning(false);
        // system under test
        ProjectService toTest = new ProjectService(null, mockGroupRepository, mockConfig, null);

        // when
        Throwable exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("one undone group");

//        //when + then
//        assertThatIllegalStateException().isThrownBy(() -> toTest.createGroup(0, LocalDateTime.now()));
//        assertThatExceptionOfType(IllegalStateException.class)
//                .isThrownBy(() -> toTest.createGroup(0, LocalDateTime.now()));
//        // or
//         assertThatThrownBy(() -> toTest.createGroup(0, LocalDateTime.now()))
//                .isInstanceOf(IllegalStateException.class);

    }
    @Test
    @DisplayName("should throw IllegalArgumentException when configuration ok an no projects for a given id")
    void createGroup_configurationOk_And_noProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository, null, mockConfig, null);

        // when
        Throwable exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should throw IllegalArgumentException when configured to allow just 1 group and no group and projects for a given id")
    void createGroup_noMultipleGroupsConfig_And_noUndoneGroupExists_And_noProjects_throwsIllegalArgumentException() {
        //given
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).thenReturn(Optional.empty());
        // and
        TaskGroupRepository mockGroupRepository = groupRepositoryReturning(false);
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository, mockGroupRepository, mockConfig, null);

        // when
        Throwable exception = catchThrowable(() -> toTest.createGroup(0, LocalDateTime.now()));

        // then
        assertThat(exception)
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("id not found");
    }

    @Test
    @DisplayName("should create a new group from project")
    void createGroup_configurationOk_existingProject_createsAndSavesGroup() {
        // given
        LocalDateTime today = LocalDate.now().atStartOfDay();
        // and
        Project project = projectWith("bar", Stream.of(-1, -2).collect(Collectors.toSet()));
        ProjectRepository mockRepository = mock(ProjectRepository.class);
        when(mockRepository.findById(anyInt())).
                thenReturn(Optional.of(project));
        // and
        InMemoryGroupRepository inMemoryGroupRepo = inMemoryGroupRepository();
        TaskGroupService serviceWithInMemoryRepo = dummyGroupService(inMemoryGroupRepo);
        int countBeforeCall = inMemoryGroupRepo.count();
        // and
        TaskConfigurationProperties mockConfig = configurationReturning(true);
        // system under test
        ProjectService toTest = new ProjectService(mockRepository, inMemoryGroupRepo, mockConfig, serviceWithInMemoryRepo);

        // when
        GroupReadModel result = toTest.createGroup(1, today);

        // then
        assertThat(result.getDescription()).isEqualTo("bar");
        assertThat(result.getDeadline()).isEqualTo(today.minusDays(1));
        assertThat(result.getTasks()).anyMatch(task -> task.getDescription().equals("..."));
        assertThat(countBeforeCall + 1)
                .isEqualTo(inMemoryGroupRepo.count());
    }

    private TaskGroupService dummyGroupService(final InMemoryGroupRepository inMemoryGroupRepo) {
        return new TaskGroupService(inMemoryGroupRepo, null);
    }

    private Project projectWith(String projectDescription, Set<Integer>daysToDeadLine){

        Project result = mock(Project.class);
        when(result.getDescription()).thenReturn(projectDescription);

        Set<ProjectStep> steps = daysToDeadLine.stream()
                .map(days -> {
                    ProjectStep step = mock(ProjectStep.class);
                    when(step.getDescription()).thenReturn("...");
                    when(step.getDaysToDeadline()).thenReturn(days);

                    return step;
                }).collect(Collectors.toSet());

        when(result.getSteps()).thenReturn(steps);
        return result;
    }

    private TaskGroupRepository groupRepositoryReturning(final boolean result) {
        TaskGroupRepository mockGroupRepository = mock(TaskGroupRepository.class);
        when(mockGroupRepository.existsByDoneIsFalseAndProject_Id(anyInt())).thenReturn(result);
        return mockGroupRepository;
    }

    private TaskConfigurationProperties configurationReturning(final boolean result) {

        TaskConfigurationProperties.Template mockTemplate = mock(TaskConfigurationProperties.Template.class);
        when(mockTemplate.isAllowMultipleTasks()).thenReturn(result);

        TaskConfigurationProperties mockConfig = mock(TaskConfigurationProperties.class);
        when(mockConfig.getTemplate()).thenReturn(mockTemplate);

        return mockConfig;
    }

    private InMemoryGroupRepository inMemoryGroupRepository() {
        return new InMemoryGroupRepository();
    }

    private static class InMemoryGroupRepository implements TaskGroupRepository {
        private int index = 0;
        private Map<Integer, TaskGroup> map = new HashMap<>();

        public int count(){
            return map.values().size();
        }

        @Override
        public List<TaskGroup> findAll() {
            return new ArrayList<>(map.values());
        }

        @Override
        public Optional<TaskGroup> findById(Integer id) {
            return Optional.ofNullable(map.get(id));
        }

        @Override
        public TaskGroup save(TaskGroup entity) {
            if (entity.getId() == 0) {
                try {
                    Field field = TaskGroup.class.getDeclaredField("id");
                    field.setAccessible(true);
                    field.set(entity, ++index);
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    throw new RuntimeException(e);
                }
            }
            map.put(entity.getId(), entity);

            return entity;
        }

        @Override
        public boolean existsByDoneIsFalseAndProject_Id(Integer groupId) {
            return map.values().stream()
                    .filter(group -> !group.isDone())
                    .anyMatch(group -> group.getProject() != null && group.getProject().getId() == groupId);
        }

        @Override
        public boolean existsByDescription(String description) {
            return map.values().stream()
                    .anyMatch(group -> group.getDescription().equals(description));
        }
    }

}