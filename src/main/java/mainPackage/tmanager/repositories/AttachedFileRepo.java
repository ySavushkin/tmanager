package mainPackage.tmanager.repositories;

import mainPackage.tmanager.models.AttachedFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachedFileRepo extends JpaRepository<AttachedFile, Integer> {
}
