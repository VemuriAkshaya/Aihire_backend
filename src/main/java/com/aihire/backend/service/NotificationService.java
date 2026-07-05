package com.aihire.backend.service;

import com.aihire.backend.dto.NotificationDto;
import com.aihire.backend.entity.User;

import java.util.List;

public interface NotificationService {
    NotificationDto createNotification(User user, String title, String message);
    List<NotificationDto> getUserNotifications(String userEmail);
    void markAsRead(Long notificationId, String userEmail);
}
