package com.example.demo;

import lombok.Builder;
import lombok.Data;

@Builder
public class Notification {
    private Member receiver;
    private Review review;
    private String content;
    private String url;
    private boolean isRead;
}
