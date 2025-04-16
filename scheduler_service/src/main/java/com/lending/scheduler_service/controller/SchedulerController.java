package com.lending.scheduler_service.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/scheduler")
public class SchedulerController {

    @GetMapping
    public ResponseEntity<?> getScheduler(){
        return ResponseEntity.ok("Scheduler service");
    }
}
