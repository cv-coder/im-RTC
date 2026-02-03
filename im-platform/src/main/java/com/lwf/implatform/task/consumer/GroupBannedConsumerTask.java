package com.lwf.implatform.task.consumer;

import com.lwf.imclient.IMClient;
import com.lwf.imcommon.enums.IMTerminalType;
import com.lwf.imcommon.model.IMGroupMessage;
import com.lwf.imcommon.model.IMUserInfo;
import com.lwf.imcommon.mq.RedisMQConsumer;
import com.lwf.imcommon.mq.RedisMQListener;
import com.lwf.implatform.contant.Constant;
import com.lwf.implatform.contant.RedisKey;
import com.lwf.implatform.dto.GroupBanDTO;
import com.lwf.implatform.entity.GroupMessage;
import com.lwf.implatform.enums.MessageStatus;
import com.lwf.implatform.enums.MessageType;
import com.lwf.implatform.service.GroupMemberService;
import com.lwf.implatform.service.GroupMessageService;
import com.lwf.implatform.util.BeanUtils;
import com.lwf.implatform.vo.GroupMessageVO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * @author: Blue
 * @date: 2024-07-15
 * @version: 1.0
 */
@Slf4j
@Component
@RequiredArgsConstructor
@RedisMQListener(queue = RedisKey.IM_QUEUE_GROUP_BANNED)
public class GroupBannedConsumerTask extends RedisMQConsumer<GroupBanDTO> {

    private final IMClient imClient;

    private final GroupMessageService groupMessageService;

    private final GroupMemberService groupMemberService;

    @Override
    public void onMessage(GroupBanDTO dto) {
        log.info("群聊被封禁处理,群id:{},原因:{}", dto.getId(), dto.getReason());
        // 群聊成员列表
        List<Long> userIds = groupMemberService.findUserIdsByGroupId(dto.getId());
        // 保存消息
        GroupMessage msg = new GroupMessage();
        msg.setGroupId(dto.getId());
        String tip = "本群聊已被管理员封禁,原因:" + dto.getReason();
        msg.setContent(tip);
        msg.setSendId(Constant.SYS_USER_ID);
        msg.setSendTime(new Date());
        msg.setStatus(MessageStatus.UNSEND.code());
        msg.setSendNickName("系统管理员");
        msg.setType(MessageType.TIP_TEXT.code());
        groupMessageService.save(msg);
        // 推送提示语到群聊中
        GroupMessageVO msgInfo = BeanUtils.copyProperties(msg, GroupMessageVO.class);
        IMGroupMessage<GroupMessageVO> sendMessage = new IMGroupMessage<>();
        sendMessage.setSender(new IMUserInfo(Constant.SYS_USER_ID, IMTerminalType.PC.code()));
        sendMessage.setRecvIds(userIds);
        sendMessage.setSendToSelf(false);
        sendMessage.setData(msgInfo);
        imClient.sendGroupMessage(sendMessage);
    }
}
