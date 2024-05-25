package mainPackage.tmanager.repositories;

import mainPackage.tmanager.enums.UserRoleInProjectE;
import mainPackage.tmanager.models.Project;
import mainPackage.tmanager.models.User;
import mainPackage.tmanager.models.UserRoleInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoleInProjectRepo extends JpaRepository<UserRoleInProject, Integer> {

    @Query("SELECT urp.user FROM UserRoleInProject urp WHERE urp.project = :project AND urp.roleInProject = :role1 OR urp.roleInProject = :role2")
    List<User> findUsersByProjectAndRoles(Project project, UserRoleInProjectE role1, UserRoleInProjectE role2);
}



