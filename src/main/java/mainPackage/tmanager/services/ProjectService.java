package mainPackage.tmanager.services;

import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProjectService {
    private final ProjectRepository projectRepository;
    @Autowired
    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Transactional
    public void save(Project project) {
        project.setCreatedAt(LocalDateTime.now());
        projectRepository.save(project);
    }

    public Optional<Project> findById(int id) {
       return projectRepository.findById(id);
    }
}
