package com.example.demo;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;

@Repository
@RequiredArgsConstructor
public class EmitterRepository {
    private final Map<String, SseEmitter> sseEmitterMap;
    public SseEmitter save(String id, SseEmitter emitter) {
        sseEmitterMap.put(id, emitter);
        return emitter;
    }

    public void deleteById(String id) {
        sseEmitterMap.remove(id);
    }
}
