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
@RedisMQListener(queue = IMRedisKey.IM_RESULT_GROUP_QUEUE, batchSize = 100)
public class GroupMessageResultResultTask extends AbstractMessageResultTask<IMSendResult> {

    private final MessageListenerMulticaster listenerMulticaster;

    @Override
    public void onMessage(List<IMSendResult> results) {
        listenerMulticaster.multicast(IMListenerType.GROUP_MESSAGE, results);
    }

}
