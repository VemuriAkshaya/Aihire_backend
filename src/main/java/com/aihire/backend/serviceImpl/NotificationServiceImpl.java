package com.aihire.backend.serviceImpl;

import com.aihire.backend.dto.NotificationDto;
import com.aihire.backend.entity.Notification;
import com.aihire.backend.entity.User;
import com.aihire.backend.exception.BadRequestException;
import com.aihire.backend.exception.ResourceNotFoundException;
import com.aihire.backend.mapper.EntityMapper;
import com.aihire.backend.repository.NotificationRepository;
import com.aihire.backend.repository.UserRepository;
import com.aihire.backend.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EntityMapper mapper;

    @Override
    @Transactional
    public NotificationDto createNotification(User user, String title, String message) {
        Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .read(false)
                .build();

        Notification saved = notificationRepository.save(notification);
        return convertToDto(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationDto> getUserNotifications(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<Notification> list = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return list.stream().map(this::convertToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        if (!notification.getUser().getEmail().equalsIgnoreCase(userEmail)) {
            throw new BadRequestException("You are not authorized to access this notification");
        }

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private NotificationDto convertToDto(Notification notification) {
        NotificationDto dto = mapper.map(notification, NotificationDto.class);
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
        }
        return dto;
    }
}
