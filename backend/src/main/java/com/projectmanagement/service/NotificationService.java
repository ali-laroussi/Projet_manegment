package com.projectmanagement.service;

import com.projectmanagement.entity.Employee;
import com.projectmanagement.entity.Notification;
import com.projectmanagement.repository.EmployeeRepository;
import com.projectmanagement.repository.NotificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    public Notification sendToEmployee(Long employeeId, String message, String senderName) {
        Employee recipient = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new IllegalArgumentException("Employe introuvable"));

        Notification notification = Notification.builder()
            .recipient(recipient)
            .message(message.trim())
            .senderName(senderName)
            .read(false)
            .build();

        return notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<Notification> findByEmployee(Long employeeId) {
        return notificationRepository.findByRecipientIdOrderByCreatedAtDesc(employeeId);
    }
}
