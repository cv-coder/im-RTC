package com.lwf.implatform.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.lwf.implatform.exception.GlobalException;
import com.lwf.implatform.result.Result;
import com.lwf.implatform.result.ResultUtils;
import com.lwf.implatform.service.WebrtcPrivateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@Tag(name = "单人通话")
@RestController
@RequestMapping("/webrtc/private")
@RequiredArgsConstructor
public class WebrtcPrivateController {

    private final WebrtcPrivateService webrtcPrivateService;

    @Operation(summary = "呼叫视频通话")
    @PostMapping("/call")
    public Result<?> call(@RequestParam Long uid, @RequestParam(defaultValue = "video") String mode,
            @RequestBody(required = false) Object offer) {
        webrtcPrivateService.call(uid, mode, normalizeSignal(offer, "offer"));
        return ResultUtils.success();
    }

    @Operation(summary = "接受视频通话")
    @PostMapping("/accept")
    public Result accept(@RequestParam Long uid, @RequestBody(required = false) Object answer) {
        webrtcPrivateService.accept(uid, normalizeSignal(answer, "answer"));
        return ResultUtils.success();
    }

    @Operation(summary = "拒绝视频通话")
    @PostMapping("/reject")
    public Result reject(@RequestParam Long uid) {
        webrtcPrivateService.reject(uid);
        return ResultUtils.success();
    }

    @Operation(summary = "取消呼叫")
    @PostMapping("/cancel")
    public Result cancel(@RequestParam Long uid) {
        webrtcPrivateService.cancel(uid);
        return ResultUtils.success();
    }

    @Operation(summary = "呼叫失败")
    @PostMapping("/failed")
    public Result failed(@RequestParam Long uid, @RequestParam String reason) {
        webrtcPrivateService.failed(uid, reason);
        return ResultUtils.success();
    }

    @Operation(summary = "挂断")
    @PostMapping("/handup")
    public Result handup(@RequestParam Long uid) {
        webrtcPrivateService.handup(uid);
        return ResultUtils.success();
    }

    @PostMapping("/candidate")
    @Operation(summary = "同步candidate")
    public Result candidate(@RequestParam Long uid, @RequestBody(required = false) Object candidate) {
        webrtcPrivateService.candidate(uid, normalizeSignal(candidate, "candidate"));
        return ResultUtils.success();
    }

    @Operation(summary = "心跳")
    @PostMapping("/heartbeat")
    public Result heartbeat(@RequestParam Long uid) {
        webrtcPrivateService.heartbeat(uid);
        return ResultUtils.success();
    }

    /**
     * 兼容前端传字符串/对象两种信令格式，避免因请求体反序列化失败直接返回500。
     */
    private String normalizeSignal(Object signal, String signalName) {
        if (signal == null) {
            throw new GlobalException(signalName + "不能为空");
        }
        String content = signal instanceof String ? (String) signal : JSON.toJSONString(signal);
        if (StrUtil.isBlank(content) || "null".equalsIgnoreCase(content)) {
            throw new GlobalException(signalName + "不能为空");
        }
        return content;
    }
}
