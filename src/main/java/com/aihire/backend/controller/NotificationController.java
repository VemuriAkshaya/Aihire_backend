package com.aihire.backend.controller;

import com.aihire.backend.dto.NotificationDto;
import com.aihire.backend.response.ApiResponse;
import com.aihire.backend.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification Controller", description = "Endpoints for viewing and updating user notifications")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get notifications for logged in user")
    public ResponseEntity<ApiResponse<List<NotificationDto>>> getMyNotifications(Principal principal) {
        List<NotificationDto> result = notificationService.getUserNotifications(principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Notifications retrieved successfully", result));
    }

    @PutMapping("/{id}/read")
    @Operation(summary = "Mark a notification as read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id, Principal principal) {
        notificationService.markAsRead(id, principal.getName());
        return ResponseEntity.ok(ApiResponse.success("Notification marked as read"));
    }
}
