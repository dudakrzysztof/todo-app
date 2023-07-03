package io.github.dudakrzysztof.todoapp.model.projection;

import io.github.dudakrzysztof.todoapp.model.Task;
import io.github.dudakrzysztof.todoapp.model.TaskGroup;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.HashSet;


import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class GroupReadModelTest {
    @Test
    @DisplayName("should create null deadline for group when no task deadline")
    void constructor_noDeadlines_createsNullDeadline(){
        //given
        TaskGroup source = new TaskGroup();
        source.setDescription("foo");
        source.setTasks(new HashSet<>(Collections.singletonList(new Task("bar", null))));

        //when
        GroupReadModel result = new GroupReadModel(source);

        //then
        assertThat(result).hasFieldOrPropertyWithValue("deadline", null);
    }

}