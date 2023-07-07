package com.example.RemoteDebugger.Respository;

import com.example.RemoteDebugger.pojo.MainObjective;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubRedditRepo extends MongoRepository<MainObjective,String> {
   List<MainObjective>findByAuthor(String author);

   void deleteByAuthor(String author);
   @Query("{'title': {$regex: '?0'}}")
   List<MainObjective>getMainObjectiveByRegEx(String name);

   @Query("{'selftext': ?0}")
   List<MainObjective> findByCustomQuery(String keyword);
   @Query("{\"match\":{\"title\":\"?0\"}}")
   List<MainObjective>  findByCustomQueryTitle(String keyword);

}
