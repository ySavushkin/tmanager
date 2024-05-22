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

    /*
    * Method should be used when person is creating Project,
    * so user automatically becomes an admin
    * */
    @PostMapping("/new/{userId}")
    public ResponseEntity<?> createProjectUser(@RequestBody @Valid Project project, BindingResult bindingResult,
                                               @RequestBody User user) {
        if (bindingResult.hasErrors()) {
            ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {

                projectService.save(project);

                UserRoleInProject userRoleInProject = new UserRoleInProject();
                userRoleInProject.setUser(user);
                userRoleInProject.setProject(project);
                userRoleInProject.setRole(mainPackage.tmanager.enums.UserRoleInProject.ADMIN);
                userRoleInProjectService.save(userRoleInProject);


            }
        return new ResponseEntity<>(HttpStatus.CREATED);
    }


    @PostMapping("/attach-people/{projectId}")
    public ResponseEntity<?> attachPeople(@PathVariable("projectId") int projectId,
                                          @RequestBody List<User> userList,
                                          @RequestBody User user) {
        Optional<Project> optionalProject = projectService.findById(projectId);

        if (optionalProject.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        if(!userList.isEmpty()){
            for(User u : userList ){
               u.setRole(mainPackage.tmanager.enums.UserRoleInProject.MEMBER);
                u.getProjects().add(optionalProject.get());
                userService.save(u);
            }
        } else {
            user.setRole(mainPackage.tmanager.enums.UserRoleInProject.MEMBER);
            user.getProjects().add(optionalProject.get());
            userService.save(user);
        }


        return new ResponseEntity<>(HttpStatus.OK);
    }
}
