package com.example.socketserver.model;

import lombok.Data;

@Data
public class Message {

    private String text;

    private String to;
}