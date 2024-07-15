package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.enums.DevelopingStatus;
import mainPackage.tmanager.enums.TaskStatus;
import mainPackage.tmanager.models.AttachedFile;
import java.nio.file.Files;


import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.repositories.TaskRepository;
import mainPackage.tmanager.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

//asd
@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;
    private final AttachedFileService attachedFileService;
    private final UserService userService;
    private final MailService mailService;
    private  final ProjectService projectService ;

    @Value("${filePath}")
    private String filePath;
    private final TaskRepository taskRepository;

    @Autowired
    public TaskController(TaskService taskService, AttachedFileService attachedFileService, UserService userService, MailService mailService, ProjectService projectService,
                          TaskRepository taskRepository) {
        this.taskService = taskService;
        this.attachedFileService = attachedFileService;
        this.userService = userService;

        this.mailService = mailService;
        this.projectService = projectService;
        this.taskRepository = taskRepository;
    }



    @PostMapping("/create/{projectId}")
    public ResponseEntity<?> createTask(@PathVariable("projectId") int projectId ,@RequestBody @Valid Task task,
                                        BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            Optional<Project> optionalProject = projectService.findById(projectId);
            if(optionalProject.isPresent()){
                task.setProject(optionalProject.get());
                task.setTaskStatus(TaskStatus.CREATED);
                task.setDevelopingStatus(DevelopingStatus.START);
                taskService.save(task);
            } else {
                return ResponseEntity.badRequest().body("Project not found");
            }

            return ResponseEntity.ok("Task was created");
        }
    }
    @PostMapping("change-developing_status")
    public ResponseEntity<?> changeDevelopingStatus(@RequestBody Task task){
        taskService.updateDevelopingStatus(task);
        return ResponseEntity.ok("Status was updated");
    }

    @PostMapping("/change-status")
    public ResponseEntity<?> changeStatus(@RequestBody Task task){
        taskService.updateStatus(task);
        return ResponseEntity.ok("Status was updated");
    }

    @PostMapping("/upload-file/{taskId}")
    public ResponseEntity<?> uploadFiles(@PathVariable int taskId, @RequestParam("files") MultipartFile[] files) {
        try {
            for (MultipartFile file : files) {
                processFile(taskId, file, new AttachedFile());
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseEntity(HttpStatus.OK));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/get-file/{fileId}")
    public ResponseEntity<?> getFile(@PathVariable int fileId) throws IOException {
        Optional<AttachedFile> attachedFile = attachedFileService.findById(fileId);

        if (attachedFile.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("File not found with id: " + fileId);
        }

        byte[] downloadFile = downloadFileFromFileDirectory(attachedFile.get().getFileName());

        return ResponseEntity.status(HttpStatus.OK)
                .contentType(MediaType.parseMediaType(attachedFile.get().getFileType()))
                .body(downloadFile);

    }

    public void processFile(int taskId, MultipartFile multipartFile, AttachedFile attachedFile) throws IOException {
        // Обработка файла и привязка к задаче
        Optional<Task> task = taskService.findById(taskId); // Получаем задачу по taskId
        if (task != null) {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            String relativePath = filePath + File.separator + fileName;
            attachedFile.setFileName(fileName);
            attachedFile.setFileType(multipartFile.getContentType());
            attachedFile.setFileLink(relativePath);
            attachedFile.setFileSize(multipartFile.getSize());
            String uploadDirectory = filePath;
            File newFile = new File(uploadDirectory, fileName);
            multipartFile.transferTo(newFile);
            Task task1 = task.get();
            attachedFile.setTask(task1); // Установка связи с задачей
            attachedFileService.save(attachedFile);
        }
    }

    public byte[] downloadFileFromFileDirectory(String fileName) throws IOException {

        Optional<AttachedFile> fileDataObj = attachedFileService.findByName(fileName);

        //first need to get the file path
        String filePath = fileDataObj.get().getFileLink();

        //got the file, now decompress it.
        byte[] imageFile = Files.readAllBytes(new java.io.File(filePath).toPath());

        return imageFile;
    }

    @PostMapping("/set-user/{taskId}")
    public ResponseEntity<?> setUser(@PathVariable("taskId") int taskId, @RequestBody User user) {
        // Проверяем, существует ли задача с заданным ID
        Optional<Task> taskOptional = taskService.findById(taskId);
        if (taskOptional.isPresent()) {
            Task task = taskOptional.get();

            // Проверяем, существует ли пользователь с заданным ID
            Optional<User> existingUserOptional = userService.findById(user.getId());
            if (existingUserOptional.isPresent()) {
                User existingUser = existingUserOptional.get();
                task.setUser(existingUser);
                taskService.save(task); // Сохраняем изменения в БД
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found with id: " + user.getId(), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Task not found with id: " + taskId, HttpStatus.NOT_FOUND);
        }
    }
    //TODO почему то Время старта задачи становится таким же как и время конца
    //Время выполнения записывается в String, только начатые часы, только целые числа
    //К примеру если задача выполнялась 2 часа 59 минут, то записано будет 2 часа
    @PostMapping("/finish-task/{taskId}")
    public ResponseEntity<?> finishTask(@PathVariable("taskId") int taskId){
        Optional<Task> optionalTask = taskService.findById(taskId);
        if(optionalTask.isPresent()){
            Task task = optionalTask.get();
            task.setTaskStatus(TaskStatus.DONE);
            task.setDevelopingStatus(DevelopingStatus.ENDED);
            LocalDateTime endedAt = LocalDateTime.now();
            task.setEndedAt(endedAt);
            Duration taskDoingDuration = Duration.between(task.getCreatedAt(), task.getEndedAt());
            long hours = taskDoingDuration.toHours();
            String taskDoingDurationStr = String.valueOf(hours);
            task.setDuration(taskDoingDurationStr);
            taskRepository.save(task);
            return ResponseEntity.ok("Task " + task.getName() + " was finished successfully!");
        } else {
            return ResponseEntity.badRequest().body("Task id not found");
        }
    }






}














