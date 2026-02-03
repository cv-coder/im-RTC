package com.lwf.imclient.task;

import com.lwf.imclient.listener.MessageListenerMulticaster;
import com.lwf.imcommon.contant.IMRedisKey;
import com.lwf.imcommon.enums.IMListenerType;
import com.lwf.imcommon.model.IMSendResult;
import com.lwf.imcommon.mq.RedisMQListener;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@RedisMQListener(queue = IMRedisKey.IM_RESULT_SYSTEM_QUEUE, batchSize = 100)
public class SystemMessageResultResultTask extends AbstractMessageResultTask<IMSendResult> {

    private final MessageListenerMulticaster listenerMulticaster;

    @Override
    public void onMessage(List<IMSendResult> results) {
        listenerMulticaster.multicast(IMListenerType.SYSTEM_MESSAGE, results);
    }

}
