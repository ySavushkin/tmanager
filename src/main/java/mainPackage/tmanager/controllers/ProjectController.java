package mainPackage.tmanager.controllers;

import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.Task;
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

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @PostMapping("/finish/{projectId}")
    public ResponseEntity<?> finishProject(@PathVariable("projectId") int projectId) {
        Optional<Project> projectOptional = projectService.findById(projectId);
        if (projectOptional.isPresent()) {
            Project project = projectOptional.get();
            //Проверка на незавершённые задачи
            for(Task t : project.getTaskList()){
                if(t.getEndedAt() == null){
                    return ResponseEntity.badRequest().body("Finish all tasks in order to finish project");
                }
            }
            project.setEndedAt(LocalDateTime.now());
            projectRepository.save(project);
            return ResponseEntity.ok("Finished");
        } else {
            return ResponseEntity.badRequest().body("Project not found");
        }
    }


    //TODO Ради этого метода в Project стоит JsonIgnore на users и taskList
    @GetMapping("/retrospective/{projectId}")
    public ResponseEntity<?> getRetrospective(@PathVariable("projectId") int projectId) {
        Optional<Project> projectOptional = projectService.findById(projectId);
        if (projectOptional.isPresent()) {
            Project foundProject = projectOptional.get();

            // Вычисляем длительность проекта
            Duration projectDuration = Duration.between(foundProject.getCreatedAt(), foundProject.getEndedAt());
            String projectDurationStr = String.valueOf(projectDuration.toHours());
            long hours = projectDuration.toHours();
            // Инициализируем переменные для подсчета задач
            int taskCounter = foundProject.getTaskList().size();
            long taskDoingDuration = 0;

            // Суммируем длительности задач
            for (Task t : foundProject.getTaskList()) {
                String durationStr = t.getDuration();
                if (durationStr != null && !durationStr.isEmpty()) {
                    taskDoingDuration += Long.parseLong(durationStr);
                }
            }

            // Вычисляем среднее время выполнения одной задачи

            long avgTimeForEachTask;
            if (taskCounter > 0 ) {
                avgTimeForEachTask = taskDoingDuration / taskCounter;
            } else {
                avgTimeForEachTask = 0;
            }

            // Подготавливаем данные для ответа
            Map<String, Object> retrospectiveData = new HashMap<>();
            retrospectiveData.put("duration", hours);
            retrospectiveData.put("taskCounter", taskCounter);
            retrospectiveData.put("allTaskDoingDuration", taskDoingDuration);
            retrospectiveData.put("avgTimeForEachTask", avgTimeForEachTask);
            retrospectiveData.put("project", foundProject);

            return ResponseEntity.ok(retrospectiveData);
        } else {
            return ResponseEntity.badRequest().body("Project not found");
        }
    }


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
