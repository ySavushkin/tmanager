package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.requests.InviteUsersRequest;
import mainPackage.tmanager.services.ProjectService;
import mainPackage.tmanager.services.UserRoleInProjectService;
import mainPackage.tmanager.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user")
public class UserController {
    private final UserService userService;
    private final ProjectService projectService;
    private final UserRoleInProjectService userRoleInProjectService;

    @Autowired
    public UserController(UserService userService, ProjectService projectService, UserRoleInProjectService userRoleInProjectService) {
        this.userService = userService;
        this.projectService = projectService;
        this.userRoleInProjectService = userRoleInProjectService;
    }

    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody @Valid User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            userService.save(user);
            return ResponseEntity.ok(user);
        }
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<?> getUser(@PathVariable("id") int id) {
        Optional<User> user = userService.findById(id);

        return ResponseEntity.ok(user);
    }


    @PostMapping("/invite-users1")
    public ResponseEntity<?> inviteUsers1(@RequestBody InviteUsersRequest inviteRequest) {
        List<User> userList = inviteRequest.getUsers();
        Project project = inviteRequest.getProject();
        Optional<Project> projectDB = projectService.findById(project.getId());

        Optional<User> requester = userService.findById(inviteRequest.getRequester().getId());


        if(userRoleInProjectService.findAllUsersByProjectAndAdminAndManager(project).contains(requester.get())){

        if (projectDB.isPresent()) {

            Project existingProject = projectDB.get();


            List<User> existingUsers = existingProject.getUsers();
            for (User newUser : userList) {
                if (!existingUsers.contains(newUser)) {
                    existingUsers.add(newUser);
                }
            }
            existingProject.setUsers(existingUsers);
            projectService.save(existingProject);


            for (User newUser : userList) {
                UserRoleInProject userRoleInProject = new UserRoleInProject(newUser, existingProject, UserRoleInProjectE.MEMBER);
                userRoleInProjectService.save(userRoleInProject);
            }
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Project not found with ID: " + project.getId());
        }
        } else {
            return ResponseEntity.badRequest().body("You are not an admin");
        }
    }

    @PostMapping("/delete-from-project")
    public ResponseEntity<?> deleteUsersFromProject(@RequestBody User admin, List<User> userList, Project project) {
        if (!(admin.getRole() == UserRoleInProjectE.ADMIN)) {
            return ResponseEntity.badRequest().body("You are not an admin ");
        } else {
            project.getUsers().remove(userList);
            projectService.save(project);
            return ResponseEntity.ok("Deleted successfully");
        }
    }
}
