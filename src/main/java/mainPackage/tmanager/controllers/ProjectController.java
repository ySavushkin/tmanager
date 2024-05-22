package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.services.ProjectService;
import mainPackage.tmanager.services.TaskService;
import mainPackage.tmanager.services.UserRoleInProjectService;
import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static mainPackage.tmanager.enums.UserRoleInProject.ADMIN;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final UserRoleInProjectService userRoleInProjectService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final UserService userService;

    @Autowired
    public ProjectController(UserRoleInProjectService userRoleInProjectService, ProjectService projectService, TaskService taskService, UserService userService) {
        this.userRoleInProjectService = userRoleInProjectService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.userService = userService;
    }

    @PostMapping("/new")
    public ResponseEntity<?> createProject(@RequestBody @Valid Project project, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            projectService.save(project);
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }
    /*
    * Method should be used when person is creating Project,
    * so he automatically becomes an admin
    * */
    @PostMapping("/new/{userId}")
    public ResponseEntity<?> createProjectUser(@RequestBody @Valid Project project, BindingResult bindingResult,
                                               @PathVariable("userId") int userId) {
        if (bindingResult.hasErrors()) {
            ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            Optional<User> user = userService.findById(userId);
            if(user.isPresent()) {

                projectService.save(project);

                UserRoleInProject userRoleInProject = new UserRoleInProject();
                userRoleInProject.setUser(user.get());
                userRoleInProject.setProject(project);
                userRoleInProject.setRole(mainPackage.tmanager.enums.UserRoleInProject.ADMIN);
                userRoleInProjectService.save(userRoleInProject);


            }
        }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PostMapping("/attach-task/{projectId}/{taskId}")
    private ResponseEntity<?> attachTask(@PathVariable("projectId") int projectId,
                                         @PathVariable("taskId") int taskId) {

        Optional<Task> task = taskService.findById(taskId);
        Optional<Project> project = projectService.findById(projectId);

        if (task.isPresent() && project.isPresent()) {


            Project project1 = project.get();
            task.get().setProject(project1);


            taskService.save(task.get());
        } else {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity(HttpStatus.OK);
    }


    @PostMapping("/attach-people/{projectId}")
    public ResponseEntity<?> attachPeople(@PathVariable("projectId") int projectId,
                                          @RequestBody User user) {
        Optional<Project> optionalProject = projectService.findById(projectId);

        if (optionalProject.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        Project project = optionalProject.get();
        project.getUsers().add(user); // Добавляем пользователя к списку пользователей проекта

        projectService.save(project); // Сохраняем проект в базе данных

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
