package io.github.dudakrzysztof.todoapp.controller;

import io.github.dudakrzysztof.todoapp.model.projection.ProjectWriteModel;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/projects")
class ProjectController {
    @GetMapping
    String showProjects(Model model){
//        ProjectWriteModel projectToEdit = new ProjectWriteModel();
//        projectToEdit.setDescription("test");
        model.addAttribute("project", new ProjectWriteModel());
        return "projects";
    }
}
