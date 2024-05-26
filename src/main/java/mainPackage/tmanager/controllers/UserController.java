package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.repositories.UserRepository;
import mainPackage.tmanager.repositories.UserRoleInProjectRepo;
import mainPackage.tmanager.requests.InviteUsersRequest;
import mainPackage.tmanager.services.ProjectService;
import mainPackage.tmanager.services.UserRoleInProjectService;
import mainPackage.tmanager.services.UserService;
import org.modelmapper.ModelMapper;
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
    private final UserRepository userRepository;
    private final UserRoleInProjectRepo userRoleInProjectRepo;

    @Autowired
    public UserController(UserService userService, ProjectService projectService, UserRoleInProjectService userRoleInProjectService,
                          UserRepository userRepository,
                          UserRoleInProjectRepo userRoleInProjectRepo) {
        this.userService = userService;
        this.projectService = projectService;
        this.userRoleInProjectService = userRoleInProjectService;
        this.userRepository = userRepository;
        this.userRoleInProjectRepo = userRoleInProjectRepo;
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

    //Метод для админа, метод для назначения менеджеров
    @PostMapping("/give-role")
    public ResponseEntity<?> giveRole(@RequestBody InviteUsersRequest inviteUsersRequest){
        Optional<User> requester = userService.findById(inviteUsersRequest.getRequester().getId());
        Optional<Project> project = projectService.findById(inviteUsersRequest.getProject().getId());
        List<User> usersToBecomeManagers = inviteUsersRequest.getUsers();
        List<User> existingUsersListToBecomeM = userService.findAllByUsers(usersToBecomeManagers);

        if(requester.isPresent() && project.isPresent()){
            User existingRequester = requester.get();
            Project existingProject = project.get();
            System.out.println(existingRequester);
            System.out.println(existingUsersListToBecomeM);
            System.out.println(existingProject);
            if(userRoleInProjectService.findAdminByProject(existingProject, UserRoleInProjectE.ADMIN).contains(existingRequester)){
                for(User u :existingUsersListToBecomeM){

                    UserRoleInProject existingUserRole = userRoleInProjectService.findByUserAndProject(u, existingProject);
                    existingUserRole.setRoleInProject(inviteUsersRequest.getUserRoleInProjectE());
                    userRoleInProjectService.save(existingUserRole);
                }
            } else {
                return ResponseEntity.badRequest().body("You are not admin");
            }
        } else {
            return ResponseEntity.badRequest().body("Requester or project not found");
        }
        return ResponseEntity.ok("Success");
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

            List<User> existingUsersListToBecomeM = userService.findAllByUsers(userList);
            List<User> existingUsers = existingProject.getUsers();
            for (User newUser : existingUsersListToBecomeM) {
                if (!existingUsers.contains(newUser)) {
                    existingUsers.add(newUser);
                    UserRoleInProject userMemberRole = new UserRoleInProject(newUser,existingProject,UserRoleInProjectE.MEMBER);
                    userRoleInProjectService.save(userMemberRole);
                }
            }
            existingProject.setUsers(existingUsers);
            projectService.save(existingProject);
            return ResponseEntity.ok("OK");
        } else {
            return ResponseEntity.badRequest().body("Project not found with ID: " + project.getId());
        }
        } else {
            return ResponseEntity.badRequest().body("You are not an admin");
        }
    }

    @PostMapping("/delete-from-project")
    public ResponseEntity<?> deleteUsersFromProject(@RequestBody InviteUsersRequest inviteUsersRequest) {
        Optional<User> requester = userService.findById(inviteUsersRequest.getRequester().getId());
        Optional<Project> project = projectService.findById(inviteUsersRequest.getProject().getId());
        List<User> usersToBeDeleted = inviteUsersRequest.getUsers();
        List<User> existingUsersListToBeDeleted = userService.findAllByUsers(usersToBeDeleted);
        if(project.isPresent()) {
            Project existingProject = project.get();
            User existingRequester = requester.get();
            if (userRoleInProjectService.findAdminByProject(existingProject, UserRoleInProjectE.ADMIN).contains(existingRequester)) {
                for(User u : existingUsersListToBeDeleted){

                    u.getProjects().remove(existingProject);
                    userService.save(u);

                    existingProject.getUsers().remove(u);
                    projectService.save(existingProject);

                    UserRoleInProject userRoleInProject = userRoleInProjectService.findByUserAndProject(u,existingProject);
                    userRoleInProjectRepo.delete(userRoleInProject);
                }

            } else {
                return ResponseEntity.badRequest().body("You are not admin");
            }

        } else {
            return ResponseEntity.badRequest().body("Project not found");
        }
        return ResponseEntity.ok("Selected users were deleted");
    }
}
