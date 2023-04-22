package io.github.dudakrzysztof.todoapp.controller;

import io.github.dudakrzysztof.todoapp.model.Task;
import io.github.dudakrzysztof.todoapp.model.TaskRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;


import java.time.LocalDateTime;
import java.util.Objects;

import static org.assertj.core.api.Assertions.*;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TaskControllerE2ETest {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    TaskRepository repo;

    @Test
    void httpGet_returnAllTasks(){
        //given
        int initial = repo.findAll().size();

        repo.save(new Task("foo", LocalDateTime.now()));
        repo.save(new Task("bar", LocalDateTime.now()));
        //when
        Task[] result = restTemplate.getForObject(getServerUrl(), Task[].class);
        //then
        assertThat(result).hasSize(initial + 2);
    }

    @Test
    void httpGet_returnTask(){
        //given
        repo.save(new Task("test1", LocalDateTime.now()));
        repo.save(new Task("test2", LocalDateTime.now()));

        //when
        Task result = restTemplate.getForObject("http://localhost:" + port + "/tasks/1", Task.class);

        //then
        assertThat(result.getDescription()).isEqualTo("Learn Java migrations");

    }

    @Test
    void httpPost_createNewTask(){
        //give
        Task task = new Task("createdToTest", LocalDateTime.now());
        HttpEntity<Task> request = new HttpEntity<>(task);

        // when
        ResponseEntity<Task> result = restTemplate.postForEntity(getServerUrl(), request, Task.class);

        //then
        assertThat(result).isNotNull();
        assertThat(result.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(result.getBody()).getDescription()).isEqualTo(task.getDescription());
    }

    private String getServerUrl(){
        return "http://localhost:" + port + "/tasks";
    }


}