package com.name.carregistry.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/noAuth")
    public String noAuth() {
        return "Everyone can see this";
    }

    @GetMapping("/userRole")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public String userRole() {
        return "Only User can see this";
    }

    @GetMapping("/adminRole")
    @PreAuthorize("hasRole('ADMIN')")
    public String adminRole() {
        return "Only Admin can see this";
    }
}