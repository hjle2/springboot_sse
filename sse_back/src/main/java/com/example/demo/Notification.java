package com.example.demo;

import lombok.Builder;
import lombok.Data;

@Builder
public class Notification {
    String memberId;
    String calendarId;
    String scheduleId;
    String date;
    int type;
}
