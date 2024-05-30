package mainPackage.tmanager.services;

import mainPackage.tmanager.enums.TaskStatus;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

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
     *
     * @param task Задача для сохранения.
     */
    @Transactional
    public void save(Task task) {
        task.setCreatedAt(LocalDateTime.now());
        taskRepository.save(task);
    }

    @Transactional
    public void updateDevelopingStatus(Task task){
        Optional<Task> existingTaskOptional = taskRepository.findById(task.getId());
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setDevelopingStatus(task.getDevelopingStatus());
            taskRepository.save(existingTask);
        } else {
            throw new RuntimeException("Task not found");
        }
    }
    @Transactional
    public void updateStatus(Task task){
        Optional<Task> existingTaskOptional = taskRepository.findById(task.getId());
        if (existingTaskOptional.isPresent()) {
            Task existingTask = existingTaskOptional.get();
            existingTask.setTaskStatus(task.getTaskStatus());
            existingTask.setStatusUpdatedAt(LocalDateTime.now());
            taskRepository.save(existingTask);
        } else {
            throw new RuntimeException("Task not found");
        }
    }




    public Optional<Task> findById(int taskId) {
        return taskRepository.findById(taskId);
    }
}


