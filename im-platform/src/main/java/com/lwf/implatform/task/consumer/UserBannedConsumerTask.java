package com.lwf.implatform.task.consumer;

import com.lwf.imclient.IMClient;
import com.lwf.imcommon.model.IMSystemMessage;
import com.lwf.imcommon.mq.RedisMQConsumer;
import com.lwf.imcommon.mq.RedisMQListener;
import com.lwf.implatform.contant.RedisKey;
import com.lwf.implatform.dto.UserBanDTO;
import com.lwf.implatform.enums.MessageType;
import com.lwf.implatform.vo.SystemMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @author: Blue
 * @date: 2024-07-15
 * @version: 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RedisMQListener(queue = RedisKey.IM_QUEUE_USER_BANNED)
public class UserBannedConsumerTask extends RedisMQConsumer<UserBanDTO> {

    private final IMClient imClient;

    @Override
    public void onMessage(UserBanDTO dto) {
        log.info("用户被封禁处理,userId:{},原因:{}", dto.getId(), dto.getReason());
        // 推送消息将用户赶下线
        SystemMessageVO msgInfo = new SystemMessageVO();
        msgInfo.setType(MessageType.USER_BANNED.code());
        msgInfo.setContent(dto.getReason());
        IMSystemMessage<SystemMessageVO> sendMessage = new IMSystemMessage<>();
        sendMessage.setRecvIds(Collections.singletonList(dto.getId()));
        sendMessage.setData(msgInfo);
        sendMessage.setSendResult(true);
        imClient.sendSystemMessage(sendMessage);
    }
}
