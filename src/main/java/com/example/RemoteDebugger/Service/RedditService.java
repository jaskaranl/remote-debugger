package com.example.RemoteDebugger.Service;

import com.example.RemoteDebugger.Respository.SubRedditRepo;
import com.example.RemoteDebugger.pojo.MainObjective;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.sun.tools.javac.Main;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

@Service
public class RedditService {
    @Autowired
    MongoClient mongoClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private SubRedditRepo repo;

    public void storeInDB(MainObjective element)
    {

        repo.insert(element);

    }

    public List<MainObjective>findWith(String author)
    {
       return repo.findByAuthor(author);
    }
    public List<MainObjective>findKeyword(String keyword)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("selftext").regex(keyword));
        return mongoTemplate.find(query, MainObjective.class);
    }

    public String deleteUsers(String name)
    {
        repo.deleteByAuthor(name);
        return "deleted";
    }

    public List<MainObjective> findWithByUsername(String username)
    {
        Query query = new Query();
        query.addCriteria(Criteria.where("author").regex(username));
        return  mongoTemplate.find(query, MainObjective.class);
    }

    public List<Document> sorting() {
        
        MongoDatabase database = mongoClient.getDatabase("Subreddit");
        MongoCollection<Document> collection = database.getCollection("mainObjective");
        List<Document> aggregationPipeline = Arrays.asList(
                new Document("$sort", new Document("createdutc", 1L))
        );
        List<Document> into = collection.aggregate(aggregationPipeline).into(new ArrayList<>());

        return into;
//        return repo.findAll(Sort.by(Sort.Direction.ASC,"createdutc"));
    }
    public List<MainObjective> all()
    {
        return repo.findAll();
    }

    public List<MainObjective> getAllByAuthorMethod(String authorName) {

        return repo.findByAuthor(authorName);
    }
    public List<MainObjective> getAllDataMethod() {

        List<MainObjective> all = repo.findAll();
        return all;
    }


    public void DeleteMethodd(String id)
    {

         repo.deleteById(id);
        return ;
        //            Mono<Void> a=repo.deleteById(id);



    }
    public void DeleteMethodAuthor(String author)
    {

        List<MainObjective> responseFromMongo = repo.findByAuthor(author);
         repo.deleteByAuthor(author);

        return ;

    }

}
