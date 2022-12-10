package com.probal.demoweb.controller;

import com.probal.demoweb.dto.request.SendMoneyRequest;
import com.probal.demoweb.service.MoneyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@CrossOrigin
@RestController
@RequiredArgsConstructor
public class MoneyController {

    private final MoneyService moneyService;
    private Map<String, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    @GetMapping("/send_money_progress")
    public SseEmitter sendMoneyEventEmitter() throws IOException {
        SseEmitter sseEmitter = new SseEmitter();
        UUID guid = UUID.randomUUID();
        sseEmitterMap.put(guid.toString(), sseEmitter);
        sseEmitter.send(SseEmitter.event().name("GUI_ID").data(guid));
        sseEmitter.onCompletion(() -> sseEmitterMap.remove(guid.toString()));
        sseEmitter.onTimeout(() -> sseEmitterMap.remove(guid.toString()));
        return sseEmitter;
    }

    @PostMapping("/send_money")
    public ResponseEntity<String> sendMoney(@RequestBody SendMoneyRequest sendMoneyRequest
            , @RequestParam("guid") String guid) throws IOException {
        String message = "";
        try {
            moneyService.sendMoney(sendMoneyRequest, sseEmitterMap.get(guid), guid);
            sseEmitterMap.remove(guid);
            message = "Money transfer successfully done";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Something went wrong";
            sseEmitterMap.remove(guid);
            return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED).body(message);
        }
    }
}
