package sideproject.gugumo.api.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import sideproject.gugumo.domain.dto.memberDto.CustomUserDetails;
import sideproject.gugumo.domain.dto.notificationdto.NotificationDto;
import sideproject.gugumo.response.ApiResponse;
import sideproject.gugumo.service.NotificationService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * @title 로그인 한 유저 sse 연결
     */
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@AuthenticationPrincipal CustomUserDetails principal,
                                @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {
        return notificationService.subscribe(principal, lastEventId);
    }

    @DeleteMapping("/notification/{noti_id}")
    public ApiResponse<String> deleteNoti(@AuthenticationPrincipal CustomUserDetails principal,
                                          @PathVariable("noti_id") Long id) {
        notificationService.deleteNotification(principal, id);

        return ApiResponse.createSuccess("알림 삭제 완료");
    }
}