package com.lwf.imserver.task;

import com.lwf.imcommon.contant.IMRedisKey;
import com.lwf.imcommon.enums.IMCmdType;
import com.lwf.imcommon.model.IMRecvInfo;
import com.lwf.imcommon.mq.RedisMQListener;
import com.lwf.imserver.netty.processor.AbstractMessageProcessor;
import com.lwf.imserver.netty.processor.ProcessorFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author: Blue
 * @date: 2024-07-16
 * @version: 1.0
 */
@Slf4j
@Component
@RedisMQListener(queue = IMRedisKey.IM_MESSAGE_SYSTEM_QUEUE, batchSize = 10)
public class PullSystemMessageTask extends AbstractPullMessageTask<IMRecvInfo> {

    @Override
    public void onMessage(IMRecvInfo recvInfo) {
        AbstractMessageProcessor processor = ProcessorFactory.createProcessor(IMCmdType.SYSTEM_MESSAGE);
        processor.process(recvInfo);
    }

}
