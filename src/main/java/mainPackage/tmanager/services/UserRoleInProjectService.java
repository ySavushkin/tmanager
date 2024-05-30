package mainPackage.tmanager.services;

import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.repositories.UserRepository;
import mainPackage.tmanager.repositories.UserRoleInProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserRoleInProjectService {
    private final UserRoleInProjectRepo userRoleInProjectRepo;
    private final UserRepository userRepository;
    @Autowired
    public UserRoleInProjectService(UserRoleInProjectRepo userRoleInProjectRepo, UserRepository userRepository) {
        this.userRoleInProjectRepo = userRoleInProjectRepo;
        this.userRepository = userRepository;
    }
    @Transactional
    public void save(UserRoleInProject userRoleInProject){
        userRoleInProjectRepo.save(userRoleInProject);
    }

    @Transactional
    public void processing(List<UserRoleInProject> userRoleInProjectList, User user, Project project, UserRoleInProjectE role){
        for(UserRoleInProject userRoleInProject : userRoleInProjectList){
            userRoleInProject.setUser(user);
            userRoleInProject.setProject(project);
            userRoleInProject.setRoleInProject(role);
        }
    }



    public List<User> findAllUsersByProjectAndAdminAndManager(Project project) {
        return userRoleInProjectRepo.findUsersByProjectAndRoles(project, UserRoleInProjectE.ADMIN, UserRoleInProjectE.MANAGER);
    }

    public List<User> findAdminByProject(Project project, UserRoleInProjectE userRoleInProjectE) {
        return userRoleInProjectRepo.findAllByProjectAndRoleInProject(project, userRoleInProjectE);
    }


    public UserRoleInProject findByUserAndProject (User user, Project project){
        return userRoleInProjectRepo.findByUserAndProject(user,project);
    }

}

