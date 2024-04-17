package com.example.socketserver.service;

import com.example.socketserver.model.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final SimpMessagingTemplate simpMessagingTemplate;

    public void sendToSpecificUser(String user, Message message) {
        simpMessagingTemplate.convertAndSendToUser(user, "/specific", message);
    }
}
