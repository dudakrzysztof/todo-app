package io.github.dudakrzysztof.todoapp;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("task")
public class TaskConfigurationProperties {
    private Template template;

    public Template getTemplate() {
        return template;
    }

    public void setTemplate(Template template) {
        this.template = template;
    }

//        public boolean isAllowMultipleTasksFromTemplate() {
//        return allowMultipleTasksFromTemplate;
//    }
//
//    public void setAllowMultipleTasksFromTemplate(final boolean allowMultipleTasksFromTemplate) {
//        this.allowMultipleTasksFromTemplate = allowMultipleTasksFromTemplate;
//    }

    public static class Template{
        private boolean allowMultipleTasks;

        public boolean isAllowMultipleTasks() {
            return allowMultipleTasks;
        }

        public void setAllowMultipleTasks(boolean allowMultipleTasks) {
            this.allowMultipleTasks = allowMultipleTasks;
        }
    }
}
