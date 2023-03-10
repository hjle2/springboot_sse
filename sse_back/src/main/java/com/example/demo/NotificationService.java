package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(Long userId, String lastEventId) {
        // 1
        String id = userId + "_" + System.currentTimeMillis();

        // 2
        SseEmitter emitter = emitterRepository.save(id, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(id, emitter));
        emitter.onTimeout(() -> emitterRepository.deleteById(id, emitter));

        // 3
        // 503 에러를 방지하기 위한 더미 이벤트 전송
        sendToClient(emitter, id, "EventStream Created. [userId=" + userId + "]");

        // 4
        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (!lastEventId.isEmpty()) {
            Map<String, Object> events = emitterRepository.findAllEventCacheStartWithId(String.valueOf(userId));
            for (Map.Entry<String, Object> entry : events.entrySet()) {
                System.out.println(entry.getKey() + " - " + entry.getValue());
            }
            events.entrySet().stream()
                    .filter(entry -> lastEventId.compareTo(entry.getKey()) != 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue()));
        }

        return emitter;
    }

    private Notification createNotification(Member receiver, Review review, String content) {
        return Notification.builder()
                .receiver(receiver)
                .content(content)
                .review(review)
                .url("/reviews/" + review.getId())
                .isRead(false)
                .build();
    }
    // 3
    private void sendToClient(SseEmitter emitter, String id, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(id)
                    .name("sse")
                    .data(data));
        } catch (IOException exception) {
            emitterRepository.deleteById(id, emitter);
            throw new RuntimeException("연결 오류!");
        }
    }
    public void send(Member receiver, Review review, String content) {
        Notification notification = createNotification(receiver, review, content);
        String id = String.valueOf(receiver.getId());

        // 로그인 한 유저의 SseEmitter 모두 가져오기
        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllStartWithById(id);
        sseEmitters.forEach(
                (key, emitter) -> {
                    // 데이터 캐시 저장(유실된 데이터 처리하기 위함)
                    emitterRepository.saveEventCache(key, notification);
                    // 데이터 전송
                    sendToClient(emitter, key, content);
//                    sendToClient(emitter, key, NotificationResponse.from(notification));
                }
        );
    }

}