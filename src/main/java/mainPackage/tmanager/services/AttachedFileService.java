package mainPackage.tmanager.services;

import mainPackage.tmanager.models.AttachedFile;
import mainPackage.tmanager.repositories.AttachedFileRepo;
import mainPackage.tmanager.repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

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
    public void save(AttachedFile file) {
        attachedFileRepo.save(file);
    }


    public Optional<AttachedFile> findById(int fileid){return attachedFileRepo.findById(fileid);}

    public Optional<AttachedFile> findByName(String fileName){return attachedFileRepo.findByFileName(fileName);}
}


