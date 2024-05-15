package mainPackage.tmanager.controllers;

import jakarta.validation.Valid;
import mainPackage.tmanager.models.AttachedFile;
import mainPackage.tmanager.models.Task;


import mainPackage.tmanager.services.AttachedFileService;
import org.springframework.beans.factory.annotation.Autowired;
import mainPackage.tmanager.services.TaskService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

//    @PostMapping
//    public ResponseEntity<?> createNewTask(@RequestBody @Valid Task task, BindingResult bindingResult,
//                                           @RequestParam AttachedFile file) {
//
//        //Validation and save task
//        if (bindingResult.hasErrors()) {
//            return ResponseEntity.badRequest().body(bindingResult.getFieldError());
//        } else {
//            //Attaching file to task if avalible
//            if(file != null){
//                attachedFileService.attachFileToTask(task,file);
//            }
//                taskService.save(task);
//            return ResponseEntity.ok().body("Task created successfully");
//        }
//    }
@PostMapping
public ResponseEntity<?> createNewTask(@RequestParam("task") @Valid Task task, BindingResult bindingResult,
                                       @RequestParam("file") MultipartFile [] files) {
        // Validation and save task
    if (bindingResult.hasErrors()) {
        return ResponseEntity.badRequest().body(bindingResult.getFieldError());
    } else {
        taskService.save(task);
        for (MultipartFile multipartFile : files) {
        taskService.attachFile(task, multipartFile);
        }
        return ResponseEntity.ok().body("Task created successfully");
    }
    }
}
