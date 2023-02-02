package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

@Component
@Slf4j
public class SseEmitters {
    private final Map<String, SseEmitter> emitterList = new HashMap<>();//new CopyOnWriteArrayList<>();
    private static final AtomicLong counter = new AtomicLong();

    SseEmitter add(String id, SseEmitter emitter) {
        this.emitterList.put(id, emitter);

        log.info("new emitter added: {}", emitter);
        log.info("emitter list size: {}", emitterList.size());

        emitter.onCompletion(() -> {
            log.info("onComplition callback");
            this.emitterList.remove(id);
        });

        emitter.onTimeout(() -> {
            log.info("onTimeout callback");
            this.emitterList.remove(id);
        });
        return emitter;
    }

    public void count() {
        long count = counter.incrementAndGet();
        emitterList.values().forEach(emitter -> {
            try {
                emitter.send(SseEmitter.event()
                        .name("count")
                        .data(count));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void remove(String id, SseEmitter emitter) {
        emitterList.remove(id);
    }
}
