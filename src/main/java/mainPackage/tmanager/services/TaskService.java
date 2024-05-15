package mainPackage.tmanager.services;

import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional(readOnly = true)
public class TaskService {
    private final TaskRepository taskRepository;
    @Autowired
    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
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

}
