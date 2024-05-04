package com.lockbox.backend.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@CrossOrigin
@RestController("/")
public class HomeController {
    @GetMapping
    public String greeting() {
        return "Welcome to the Lockbox service";
    }

}
