package mainPackage.tmanager.controllers;

import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.repositories.ProjectRepository;
import mainPackage.tmanager.services.ProjectService;
import mainPackage.tmanager.services.TaskService;
import mainPackage.tmanager.services.UserRoleInProjectService;
import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/project")
public class ProjectController {
    private final UserRoleInProjectService userRoleInProjectService;
    private final ProjectService projectService;
    private final TaskService taskService;
    private final UserService userService;
    private final ProjectRepository projectRepository;

    @Autowired
    public ProjectController(UserRoleInProjectService userRoleInProjectService, ProjectService projectService, TaskService taskService, UserService userService,
                             ProjectRepository projectRepository) {
        this.userRoleInProjectService = userRoleInProjectService;
        this.projectService = projectService;
        this.taskService = taskService;
        this.userService = userService;
        this.projectRepository = projectRepository;
    }

    /*
    * Method should be used when person is creating Project,
    * so user automatically becomes an admin
    * */
    @PostMapping("/new")
    public ResponseEntity<?> createProjectUser(@RequestBody Project project) {

        projectService.save(project);

        List<User> userList = project.getUsers();
        UserRoleInProject userRoleInProject = new UserRoleInProject();
        userRoleInProject.setUser(userList.get(0));
        userRoleInProject.setProject(project);
        userRoleInProject.setRoleInProject(UserRoleInProjectE.ADMIN);
        userRoleInProjectService.save(userRoleInProject);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

//TODO починить постмаппинг на прикрипление людей


//    @PostMapping("/attach-people/{projectId}")
//    public ResponseEntity<?> attachPeople(@PathVariable("projectId") int projectId,
//                                          @RequestBody List<User> userList,
//                                          @RequestBody User user) {
//        Optional<Project> optionalProject = projectService.findById(projectId);
//
//        if (optionalProject.isEmpty()) {
//            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//        }
//
//        if(!userList.isEmpty()){
//            for(User u : userList ){
//               u.setRoles(UserRoleInProjectE.MEMBER);
//                u.getProjects().add(optionalProject.get());
//                userService.save(u);
//            }
//        } else {
//            user.setRoles(UserRoleInProjectE.MEMBER);
//            user.getProjects().add(optionalProject.get());
//            userService.save(user);
//        }
//
//
//        return new ResponseEntity<>(HttpStatus.OK);
//    }


}
