package com.example.remotedebugger.controller;

import com.example.remotedebugger.Service.RedditService;
import com.example.remotedebugger.javaagent.CustomTransformer;
import com.example.remotedebugger.javaagent.javaAgent;
import com.example.remotedebugger.pojo.BreakpointResponse;
import com.example.remotedebugger.pojo.Child;
import com.example.remotedebugger.pojo.MainObjective;
import com.example.remotedebugger.pojo.RedditResponse;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.instrument.UnmodifiableClassException;
import java.util.*;

import org.bson.Document;
@RestController
public class CommentController {
    String token;
    private  static Set<String> methodModifiedSet=new HashSet<>();
    @Autowired
    private RedditService redService;

    @GetMapping("/")
    public String homePage() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("E7LuXktBtTf_1rm1bDopgQ", "exavXudnpDU_wexKGONVSw5Opyc1lA");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=password&username=Correct_Jury_3674&password=jaanu@321";
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);

        Map<String,String> map;
        try {
            ObjectMapper mapper=new ObjectMapper();
            map=mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {
            });
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        token=map.get("access_token");
        return "homePage";
    }

    @GetMapping("/javaagent/{value}/{number}")
    public static String greet(@PathVariable String value,@PathVariable String number) {
        if (value.equals("200"))
            return "help";

        greet(number, value);
        return "greetPage";
    }
    @GetMapping("/getdata")
    public RedditResponse fetchDataFromReddit() {

        String token1="Bearer "+token;
        RestTemplate restTemplate=new RestTemplate();
        String url="https://oauth.reddit.com/r/appletv/new?limit=100";
        HttpHeaders headers=new HttpHeaders();

        headers.add("Authorization", token1);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);

        ResponseEntity<RedditResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, RedditResponse.class);
        final int SIZE=responseEntity.getBody().getData().getChildren().size();
        List<Child> children = responseEntity.getBody().getData().getChildren();
        for(int i=0;i<SIZE;i++) {
            redService.storeInDB(children.get(i).getData());
        }
        return responseEntity.getBody();
    }

    @GetMapping("/db/findall")
    public List<MainObjective>getAllDataMethod() {
        List<MainObjective> result=redService.getAllDataMethod();
        return result;
    }

    @GetMapping("db/find/{AuthorName}")
    public List<MainObjective> findByAuthorNameMethod(@PathVariable String AuthorName) {
      return redService.getAllByAuthorMethod(AuthorName);
    }

    @DeleteMapping("/db/delete/{id}")
    public void deleteDataMethod(@PathVariable String id)
    {
        redService.DeleteMethodd(id);
    }

    @DeleteMapping("/db/delete/author/{author}")
    public void deleteDataMethodAuthor(@PathVariable String author) {
        redService.DeleteMethodAuthor(author);
    }


    @GetMapping("db/all/{username}")
    public List<MainObjective> findWithByUsername(@PathVariable String username) {
      //delete this line
      return redService.findWithByUsername(username);
    }
    @GetMapping("db/all/")
    public List<MainObjective> allFind() {
        return redService.all();
    }

    @GetMapping("db/sort")
    public List<Document>sorting() {
      return redService.sorting();
    }

    @PostMapping("reddit/post")
    public String postReddit() {

      HttpHeaders head=new HttpHeaders();
      head.setBearerAuth(token);
      HttpEntity<String > request=new HttpEntity<>(head);
      RestTemplate a=new RestTemplate();
      Scanner sc=new Scanner(System.in);
      System.out.println("enter the subreddit");
      String sr=sc.nextLine();
      System.out.println("enter the title ");
      String title=sc.nextLine();
      System.out.println("enter the text ");
      String text=sc.nextLine();

      String url="https://oauth.reddit.com/api/submit?sr="+sr+"&kind=self&text="+text+"&title="+title;

      ResponseEntity<RedditResponse> b=a.exchange(url,HttpMethod.POST,request, RedditResponse.class);

      return "submit";

    }
    @PostMapping("/addbreakpoint")
    public String breakpoint(@RequestBody BreakpointResponse  response) throws ClassNotFoundException, UnmodifiableClassException {
        String className=response.getClassName();
        String methodName=response.getMethodName();
        if(methodModifiedSet.contains(methodName)){
            return "already added";
        }
        methodModifiedSet.add(methodName);
        CustomTransformer customTransformer=new CustomTransformer(className,methodName);
        javaAgent.instrumentation.addTransformer(customTransformer,true);
        javaAgent.instrumentation.retransformClasses(Class.forName(className.replace('/','.')));
        return "added breakpoint";
    }

}
