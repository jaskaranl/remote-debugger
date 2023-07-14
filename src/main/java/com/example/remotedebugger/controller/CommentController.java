package com.example.remotedebugger.controller;

import com.example.remotedebugger.Service.RedditService;
import com.example.remotedebugger.javaagent.AgentForInstrumentation;
import com.example.remotedebugger.javaagent.CustomTransformers;
import com.example.remotedebugger.pojo.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.lang.instrument.UnmodifiableClassException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@RestController
public class CommentController {
    String token;
    private ExecutorService executorService = Executors.newFixedThreadPool(20);
    private  HashMap<String, List<CustomTransformers>>  classesModified = new HashMap<>();
    @Autowired
    private RedditService redService;

    @GetMapping("/")
    public String homePage() {

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setBasicAuth("OQSJpmuc0qecOsezZApf-w", "YNEaPZdhRUnQ8Hq8DsJC-kgLKUag2g");
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String body = "grant_type=password&username=Correct_Jury_3674&password=jaanu@321";
        HttpEntity<String> request = new HttpEntity<>(body, headers);
        String authUrl = "https://www.reddit.com/api/v1/access_token";
        ResponseEntity<String> response = restTemplate.postForEntity(authUrl, request, String.class);
        Map<String, String> map;

        try {
            ObjectMapper mapper = new ObjectMapper();
            map = mapper.readValue(response.getBody(), new TypeReference<Map<String, String>>() {
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        token = map.get("access_token");
        return "homePage";
    }

    @GetMapping("/javaagent/{value}/{number}")
    public static String greet(@PathVariable int value, @PathVariable String number) {
        return "greetPage";
    }

    @GetMapping("/getdata")
    public RedditResponse fetchDataFromReddit() {

        String token1 = "Bearer " + token;
        RestTemplate restTemplate = new RestTemplate();
        String url = "https://oauth.reddit.com/r/appletv/new?limit=100";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", token1);
        HttpEntity<String> requestEntity = new HttpEntity<>(headers);
        ResponseEntity<RedditResponse> responseEntity = restTemplate.exchange(url, HttpMethod.GET, requestEntity, RedditResponse.class);
        final int SIZE = responseEntity.getBody().getData().getChildren().size();
        List<Child> children = responseEntity.getBody().getData().getChildren();
        for (int i = 0; i < SIZE; i++) {
            redService.storeInDB(children.get(i).getData());
        }
        return responseEntity.getBody();
    }

    @GetMapping("/db/findall")
    public List<MainObjective> getAllDataMethod() {
        List<MainObjective> result = redService.getAllDataMethod();
        return result;
    }

    @GetMapping("db/find/{AuthorName}")
    public List<MainObjective> findByAuthorNameMethod(@PathVariable String AuthorName) {
        return redService.getAllByAuthorMethod(AuthorName);
    }

    @DeleteMapping("/db/delete/{id}")
    public void deleteDataMethod(@PathVariable String id) {
        redService.DeleteMethodd(id);
    }

    @DeleteMapping("/db/delete/author/{author}")
    public void deleteDataMethodAuthor(@PathVariable String author) {
        redService.DeleteMethodAuthor(author);
    }


    @GetMapping("db/all/{username}")
    public List<MainObjective> findWithByUsername(@PathVariable String username) {
        return redService.findWithByUsername(username);
    }

    @GetMapping("db/all/")
    public List<MainObjective> allFind() {
        return redService.all();
    }

    @GetMapping("db/sort")
    public List<Document> sorting() {
        return redService.sorting();
    }

    @PostMapping("reddit/post")
    public String postReddit() {
        HttpHeaders head = new HttpHeaders();
        head.setBearerAuth(token);
        HttpEntity<String> request = new HttpEntity<>(head);
        RestTemplate restTemplate = new RestTemplate();
        Scanner sc = new Scanner(System.in);
        System.out.println("enter the subreddit");
        String sr = sc.nextLine();
        System.out.println("enter the title ");
        String title = sc.nextLine();
        System.out.println("enter the text ");
        String text = sc.nextLine();
        String url = "https://oauth.reddit.com/api/submit?sr=" + sr + "&kind=self&text=" + text + "&title=" + title;
        ResponseEntity<RedditResponse> responseBackFromReddit = restTemplate.exchange(url, HttpMethod.POST, request, RedditResponse.class);
        return "submit";
    }

    @PostMapping("/addbreakpoint")
    public String breakpoint(@RequestBody MultiBreakpoint response) throws ClassNotFoundException, UnmodifiableClassException {
        List<BreakpointResponse> responseList = response.getResponseList();
        List<Future<?>> futures = new ArrayList<>();
        for (BreakpointResponse breakpointResponse : responseList) {
            futures.add(executorService.submit(() -> {
                String className = breakpointResponse.getClassName();
                List<MethodInfo> method = breakpointResponse.getMethod();
                CustomTransformers customTransformers = new CustomTransformers();
                customTransformers.setClassName(className);
                customTransformers.setMethod(method);
                if (classesModified.containsKey(className)) {
                    classesModified.get(className).add(customTransformers);
                } else {
                    List<CustomTransformers> add = new ArrayList<>();
                    add.add(customTransformers);
                    classesModified.put(className, add);
                }
                try {
                    AgentForInstrumentation.getInstrumentation().addTransformer(customTransformers, true);
                    AgentForInstrumentation.getInstrumentation().retransformClasses(Class.forName(className.replace("/", ".")));
                    List<CustomTransformers> add = new ArrayList<>();
                    classesModified.put(className, add);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return "added breakpoint";
    }

    @GetMapping("remove/breakpoint/{classname}")
    public void removeBreakpoint(@PathVariable String classname) {

        String temporaryClassname = classname.replace(",", "/");
        classesModified.get(temporaryClassname)
                .stream()
                .forEach(e -> AgentForInstrumentation.getInstrumentation().removeTransformer(e));

        classesModified.remove(temporaryClassname);
        return;
    }
}

