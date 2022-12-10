package com.probal.demoweb.service;

import com.probal.demoweb.dto.request.SendMoneyRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class MoneyService {
    public void sendMoney(SendMoneyRequest sendMoneyRequest,
                          SseEmitter sseEmitter,
                          String guid) throws IOException {
        int range = 100;
        int count = 0;

        for (int i = 0; i < range; i+=10) {
            count += i;
            sseEmitter.send(SseEmitter.event().name(guid).data(count));
        }
        sseEmitter.send(SseEmitter.event().name(guid).data(100));
    }
}
