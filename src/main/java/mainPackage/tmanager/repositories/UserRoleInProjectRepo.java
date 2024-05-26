package mainPackage.tmanager.repositories;

import jakarta.transaction.Transactional;
import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleInProjectRepo extends JpaRepository<UserRoleInProject, Integer> {

    @Query("SELECT urp.user FROM UserRoleInProject urp WHERE urp.project = :project AND urp.roleInProject = :role1 OR urp.roleInProject = :role2")
    List<User> findUsersByProjectAndRoles(Project project, UserRoleInProjectE role1, UserRoleInProjectE role2);

//    List<User> findAllByProjectAndRoleInProject(Project project, UserRoleInProjectE userRoleInProjectE);

    @Query("SELECT urp.user FROM UserRoleInProject urp WHERE urp.project = :project AND urp.roleInProject = :roleInProject")
    List<User> findAllByProjectAndRoleInProject(@Param("project") Project project, @Param("roleInProject") UserRoleInProjectE userRoleInProjectE);

    UserRoleInProject findByUserAndProject(User user, Project project);


}





