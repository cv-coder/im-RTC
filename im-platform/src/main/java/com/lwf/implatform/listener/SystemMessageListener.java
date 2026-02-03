package com.lwf.implatform.listener;

import com.lwf.imclient.annotation.IMListener;
import com.lwf.imclient.listener.MessageListener;
import com.lwf.imcommon.enums.IMListenerType;
import com.lwf.imcommon.enums.IMSendCode;
import com.lwf.imcommon.model.IMSendResult;
import com.lwf.implatform.vo.SystemMessageVO;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
@IMListener(type = IMListenerType.SYSTEM_MESSAGE)
public class SystemMessageListener implements MessageListener<SystemMessageVO> {

    @Override
    public void process(List<IMSendResult<SystemMessageVO>> results) {
        for (IMSendResult<SystemMessageVO> result : results) {
            SystemMessageVO messageInfo = result.getData();
            if (result.getCode().equals(IMSendCode.SUCCESS.code())) {
                log.info("消息送达，消息id:{},接收者:{},终端:{}", messageInfo.getId(), result.getReceiver().getId(),
                        result.getReceiver().getTerminal());
            }
        }
    }
}
