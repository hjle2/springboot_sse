package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    private final SseEmitters sseEmitterss;
    public SseEmitter save(String id, SseEmitter emitter) {
        sseEmitterss.add(id, emitter);
        return emitter;
    }

    public void deleteById(String id, SseEmitter emitter) {
        sseEmitterss.remove(id, emitter);
    }

    public Map<String, Object> findAllEventCacheStartWithId(String valueOf) {
        return null;
    }
    public Map<String, SseEmitter> findAllStartWithById(String valueOf) {
        return null;
    }

    public void saveEventCache(String key, Notification notification) {
    }
}
