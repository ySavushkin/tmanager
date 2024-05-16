package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.models.AttachedFile;
import java.nio.file.Files;
import java.nio.file.Paths;


import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.services.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import mainPackage.tmanager.services.TaskService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final TaskService taskService;
    private final AttachedFileService attachedFileService;

    @Autowired
    public TaskController(TaskService taskService, AttachedFileService attachedFileService) {
        this.taskService = taskService;
        this.attachedFileService = attachedFileService;
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
    @PostMapping("/create")
    public ResponseEntity<?> createTask(@RequestBody @Valid Task task) {
        taskService.save(task);
        return ResponseEntity.status(HttpStatus.OK).body(new ResponseEntity(HttpStatus.OK));
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

    public void processFile(int taskId, MultipartFile multipartFile, AttachedFile attachedFile) throws IOException {
        // Обработка файла и привязка к задаче
        Optional<Task> task = taskService.findById(taskId); // Получаем задачу по taskId
        if (task != null) {
            String fileName = UUID.randomUUID() + "_" + multipartFile.getOriginalFilename();
            String relativePath = "Files/" + fileName;
            attachedFile.setFileName(multipartFile.getOriginalFilename());
            attachedFile.setFileType(multipartFile.getContentType());
            attachedFile.setFileLink(relativePath);
            attachedFile.setFileSize(multipartFile.getSize());

            String uploadDirectory = "/Users/rusleak/IdeaDoNotDeleteProjects/tmanager/src/main/resources/Files";
            File newFile = new File(uploadDirectory, fileName);
            multipartFile.transferTo(newFile);
            Task task1 = task.get();
            attachedFile.setTask(task1); // Установка связи с задачей
            attachedFileService.save(attachedFile);
        }
    }

}














