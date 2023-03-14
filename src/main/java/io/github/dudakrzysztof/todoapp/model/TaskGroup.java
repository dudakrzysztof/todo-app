package io.github.dudakrzysztof.todoapp.model;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import java.util.Set;


@Entity
@Table(name = "task_groups")
public class TaskGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @NotBlank(message = "Task group's description must not be empty")
    private String description;
    private boolean done;
//    @Embedded
//    private Audit audit = new Audit();
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "group")
    private Set<Task> tasks;


    public TaskGroup() {
    }

    public int getId() {
        return id;
    }

    void setId(final int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    void setDescription(String description) {
        this.description = description;
    }

    public boolean isDone() {
        return done;
    }

    public void setDone(boolean done) {
        this.done = done;
    }

    public void setTasks(final Set<Task> tasks) {
        this.tasks = tasks;
    }

    public Set<Task> getTasks() {
        return tasks;
    }
}
