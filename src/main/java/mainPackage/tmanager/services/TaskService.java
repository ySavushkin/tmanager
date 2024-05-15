package mainPackage.tmanager.services;

import mainPackage.tmanager.models.AttachedFile;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;
    private final AttachedFileService attachedFileService;
    @Autowired
    public TaskService(TaskRepository taskRepository, AttachedFileService attachedFileService) {
        this.taskRepository = taskRepository;
        this.attachedFileService = attachedFileService;
    }

    /**
     * Сохраняет задачу в репозитории и устанавливает время создания.
     * @param task Задача для сохранения.
     */
    @Transactional
    public void save(Task task){
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional
    public void saveTaskWithFile(Task task, MultipartFile file) {
        // Save the task
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);

        // Process and save the file
        if (!file.isEmpty()) {
            AttachedFile attachedFile = attachedFileService.processFile(file);
            attachedFileService.save(attachedFile);
            attachedFileService.attachFileToTask(task, attachedFile);
        }
    }
}
