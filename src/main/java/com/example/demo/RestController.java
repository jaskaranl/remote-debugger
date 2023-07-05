package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.logging.Logger;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    @GetMapping("/greet/{value}/{number}")
    public String greet(@PathVariable String value, @PathVariable String number) {
        if (value.equals("200"))
            return "help";
        greet(number, value);

        return "greetPage";
    }

    @GetMapping("home")
    public String homePage() {
        return "homePage";
    }
}


