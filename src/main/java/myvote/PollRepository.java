package myvote;

import java.util.ArrayList;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "polls", path="polls")
public interface PollRepository extends MongoRepository<Poll, String>{
	
	Poll findById(@Param("id") String pollId);
	ArrayList<Poll> findByModeratorId(@Param("moderatorId") Integer moderatorId);

}
