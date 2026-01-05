package com.vbs.demo.controller;

import com.vbs.demo.models.Notification;
import com.vbs.demo.repositories.NotificationRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*")
public class NotificationController {

    @Autowired
    NotificationRepo notificationRepo;

    // 🔔 Customer notifications
    @GetMapping("/notifications/{userId}")
    public List<Notification> getNotifications(@PathVariable int userId) {
        return notificationRepo.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
