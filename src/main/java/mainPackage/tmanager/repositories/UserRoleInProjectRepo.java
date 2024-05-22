package mainPackage.tmanager.repositories;

import mainPackage.tmanager.models.UserRoleInProject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRoleInProjectRepo extends JpaRepository<UserRoleInProject, Integer> {
}
