package io.github.dudakrzysztof.todoapp.controller;

import io.github.dudakrzysztof.todoapp.TaskConfigurationProperties;

import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InfoController {

    private DataSourceProperties dataSource;
    private TaskConfigurationProperties myProp;

    public InfoController(final DataSourceProperties dataSource, final TaskConfigurationProperties myProp) {
        this.dataSource = dataSource;
        this.myProp = myProp;
    }

    @GetMapping("/info/url")
    String url(){
        return dataSource.getUrl();
    }

    @GetMapping("/info/prop")
    boolean myProp(){
        return myProp.getTemplate().isAllowMultipleTasks();
    }
}
