package myvote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "moderators", path="moderators")
interface ModeratorRepository extends  MongoRepository<Moderator, String> {
 
	Moderator findById(@Param("id") Integer id);
 
	void delete(Moderator deleted);
 
    List<Moderator> findAll();
 
    Moderator findOne(String id);
 
    Moderator save(Moderator saved);
}