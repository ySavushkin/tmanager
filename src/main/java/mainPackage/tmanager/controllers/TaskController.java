package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.models.AttachedFile;
import java.nio.file.Files;


import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.models.User;
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
import java.util.*;

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

    @Autowired
    public TaskController(TaskService taskService, AttachedFileService attachedFileService, UserService userService, MailService mailService, ProjectService projectService) {
        this.taskService = taskService;
        this.attachedFileService = attachedFileService;
        this.userService = userService;

        this.mailService = mailService;
        this.projectService = projectService;
    }

    //--------------------------------------------------------------------------------------------------
//    @PostMapping("/upload-file")
//    public ResponseEntity<?> uploadFiles(@RequestParam("files") MultipartFile[] files) {
//        // Определяем директорию, в которую будут загружены файлы
//
//        try {
//
//            for (MultipartFile file : files) {
//                processFile(file, new AttachedFile());
//            }
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseEntity(HttpStatus.OK));
//        } catch (IOException e) {
//            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(HttpStatus.BAD_REQUEST);
//        }
//    }
//    @PostMapping("/create")
//    public ResponseEntity<?> createTask(@RequestBody @Valid Task task){
//            taskService.save(task);
//            return ResponseEntity.status(HttpStatus.OK).body(new ResponseEntity(HttpStatus.OK));
//    }
//    public void processFile(MultipartFile multipartFile, AttachedFile attachedFile) throws IOException {
//    String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
//    //Добавляем данные в БД
//    String relativePath = "Files/" + fileName;
//    attachedFile.setFileName(multipartFile.getOriginalFilename());
//    attachedFile.setFileType(multipartFile.getContentType());
//    attachedFile.setFileLink(relativePath);
//    attachedFile.setFileSize(multipartFile.getSize());
//    //Отправляем файл в папку Files на хранение
//    String uploadDirectory = "/Users/rusleak/IdeaDoNotDeleteProjects/tmanager/src/main/resources/Files";
//    File newFile = new File(uploadDirectory, fileName);
//    //Отправляет файл в по сути созданную директорию newFile
//    multipartFile.transferTo(newFile);
//    //Сохраняем в БД
//    attachedFileService.save(attachedFile);
//}
    //--------------------------------------------------------------------------------------------------
    @PostMapping("/create/{projectId}")
    public ResponseEntity<?> createTask(@RequestBody @Valid Task task,
                                        BindingResult bindingResult,
                                        @PathVariable("projectId") int projectId) {
        if(bindingResult.hasErrors()) {
            return ResponseEntity.badRequest().body(bindingResult.getFieldError().getDefaultMessage());
        } else {
            Optional<Project> project = projectService.findById(projectId);
            task.setProject(project.get());
            taskService.save(task);
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseEntity(HttpStatus.OK));
        }
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
                //----------------
                //НУЖНо allow less secure app
                //НО ЭТА ФУНКЦИЯ ОТКЛЮЧЕНА В гугл
//                mailService.sendEmail(existingUser);
                //----------------
                taskService.save(task); // Сохраняем изменения в БД
                return new ResponseEntity<>(HttpStatus.OK);
            } else {
                return new ResponseEntity<>("User not found with id: " + user.getId(), HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>("Task not found with id: " + taskId, HttpStatus.NOT_FOUND);
        }
    }




}














