package ru.gormikle.eduhub.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class TestSecurityController {
    @GetMapping("/unsecured")
    public String unsecuredData(){
        return "Unsecured data";
    }
    @GetMapping("/secured")
    public String securedData(){
        return "Secured data";
    }
    @GetMapping("/admin")
    public String adminData(){
        return "Admin data";
    }
    @GetMapping("/info")
    public String userData(Principal principal){
        return principal.getName();
    }
}
