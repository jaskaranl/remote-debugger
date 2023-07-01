package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RestController;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    public String homePage()
    {
        return "homepage";
    }
    @GetMapping("/greet")
    public String greet()
    {
        return "greetPage";
    }
@GetMapping("help")
    public String help()
    {
        return "homePage";
    }

}
