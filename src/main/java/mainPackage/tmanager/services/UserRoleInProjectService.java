package mainPackage.tmanager.services;

import mainPackage.tmanager.models.UserRoleInProject;
import mainPackage.tmanager.repositories.UserRoleInProjectRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class UserRoleInProjectService {
    private final UserRoleInProjectRepo userRoleInProjectRepo;
    @Autowired
    public UserRoleInProjectService(UserRoleInProjectRepo userRoleInProjectRepo) {
        this.userRoleInProjectRepo = userRoleInProjectRepo;
    }
    @Transactional
    public void save(UserRoleInProject userRoleInProject){
        userRoleInProjectRepo.save(userRoleInProject);
    }
}
