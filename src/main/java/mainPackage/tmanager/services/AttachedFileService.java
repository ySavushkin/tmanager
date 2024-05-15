package mainPackage.tmanager.services;

import mainPackage.tmanager.models.AttachedFile;
import mainPackage.tmanager.models.Task;
import mainPackage.tmanager.repositories.AttachedFileRepo;
import mainPackage.tmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;

@Service
@Transactional(readOnly = true)
public class AttachedFileService {
    private final AttachedFileRepo attachedFileRepo;
    private final TaskRepository taskRepository;
@Autowired
    public AttachedFileService(AttachedFileRepo attachedFileRepo, TaskRepository taskRepository) {
        this.attachedFileRepo = attachedFileRepo;
    this.taskRepository = taskRepository;
}

    @Transactional
    public void attachFileToTask(Task task, AttachedFile file){
    if(task.getAttachedFiles().isEmpty()) {
        ArrayList<AttachedFile> fileList = new ArrayList<>();
        fileList.add(file);
        task.setAttachedFiles(fileList);
    }
}

    @Transactional
    public void  save(AttachedFile file){
    attachedFileRepo.save(file);
    }
    @Transactional
    public AttachedFile processFile(MultipartFile file) {
        AttachedFile attachedFile = new AttachedFile();
        attachedFile.setFileName(file.getOriginalFilename());
        attachedFile.setFileSize(file.getSize());
        attachedFile.setFileType(file.getContentType());
        return attachedFile;
    }
}
