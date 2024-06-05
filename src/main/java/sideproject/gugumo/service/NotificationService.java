package sideproject.gugumo.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import sideproject.gugumo.domain.dto.CustomUserDetails;
import sideproject.gugumo.domain.entity.Member;
import sideproject.gugumo.domain.entity.Notification;
import sideproject.gugumo.repository.EmitterRepository;
import sideproject.gugumo.repository.NotificationRepository;

import java.io.IOException;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final Long DEFAULT_TIMEOUT = 60L * 1000 * 60;

    private final NotificationRepository notificationRepository;
    private final EmitterRepository emitterRepository;

    public SseEmitter subscribe(CustomUserDetails principal, String lastEventId) {

        String emitterId = principal.getUsername() + "_" + System.currentTimeMillis();

        SseEmitter emitter = emitterRepository.save(emitterId, new SseEmitter(DEFAULT_TIMEOUT));

        emitter.onCompletion(() -> emitterRepository.deleteById(emitterId));
        emitter.onTimeout(() -> emitterRepository.deleteById(emitterId));

        // 503 에러를 방지하기 위한 더미 이벤트 전송
        String eventId = principal.getUsername() + "_" + System.currentTimeMillis();
        sendNotification(emitter, eventId, emitterId,
                "EventStream Created. [userEmail=" + principal.getUsername() + "]");

        // 클라이언트가 미수신한 Event 목록이 존재할 경우 전송하여 Event 유실을 예방
        if (hasLostData(lastEventId)) {
            sendLostData(lastEventId, principal.getUsername(), emitterId, emitter);
        }

        return emitter;

    }


    //TODO: 미완성
//    public void send(Member receiver, String content, Long commentId) {
//
//        Notification notification = Notification.builder()
//                .member(receiver)
//                .content(content)
//                .commentId(commentId)
//                .build();
//
//        notificationRepository.save(notification);
//
//        String receiverEmail = receiver.getUsername();
//        String eventId = receiverEmail + "_" + System.currentTimeMillis();
//        Map<String, SseEmitter> emitters = emitterRepository.findAllEmitterStartWithByMemberId(receiverEmail);
//        emitters.forEach(
//                (key, emitter) -> {
//                    emitterRepository.saveEventCache(key, notification);
//                    sendNotification(emitter, eventId, key, NotifyDto.Response.createResponse(notification));
//                }
//        );
//    }

    private boolean hasLostData(String lastEventId) {
        return !lastEventId.isEmpty();
    }

    private void sendLostData(String lastEventId, String userEmail, String emitterId, SseEmitter emitter) {
        Map<String, Object> eventCaches = emitterRepository.findAllEventCacheStartWithByMemberId(String.valueOf(userEmail));
        eventCaches.entrySet().stream()
                .filter(entry -> lastEventId.compareTo(entry.getKey()) < 0)
                .forEach(entry -> sendNotification(emitter, entry.getKey(), emitterId, entry.getValue()));
    }

    private void sendNotification(SseEmitter emitter, String eventId, String emitterId, Object data) {
        try {
            emitter.send(SseEmitter.event()
                    .id(eventId)
                    .name("sse")
                    .data(data)
            );
        } catch (IOException exception) {
            emitterRepository.deleteById(emitterId);
        }
    }
}