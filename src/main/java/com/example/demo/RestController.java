package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
//import org.springframework.web.bind.annotation.RestController;

@org.springframework.web.bind.annotation.RestController
public class RestController {


    public String homePage()
    {
        return "homepage";
    }
    @GetMapping("/greet/{value}")
    public String greet(@PathVariable String value)
    {

        return "greetPage";
    }
@GetMapping("help")
    public String help()
    {
        return "homePage";
    }

}
