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
@RedisMQListener(queue = IMRedisKey.IM_RESULT_PRIVATE_QUEUE, batchSize = 100)
public class PrivateMessageResultResultTask extends AbstractMessageResultTask<IMSendResult> {

    private final MessageListenerMulticaster listenerMulticaster;

    @Override
    public void onMessage(List<IMSendResult> results) {
        listenerMulticaster.multicast(IMListenerType.PRIVATE_MESSAGE, results);
    }

}
