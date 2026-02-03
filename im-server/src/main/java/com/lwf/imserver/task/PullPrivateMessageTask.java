package com.lwf.imserver.task;

import com.lwf.imcommon.contant.IMRedisKey;
import com.lwf.imcommon.enums.IMCmdType;
import com.lwf.imcommon.model.IMRecvInfo;
import com.lwf.imcommon.mq.RedisMQListener;
import com.lwf.imserver.netty.processor.AbstractMessageProcessor;
import com.lwf.imserver.netty.processor.ProcessorFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@RedisMQListener(queue = IMRedisKey.IM_MESSAGE_PRIVATE_QUEUE, batchSize = 10)
public class PullPrivateMessageTask extends AbstractPullMessageTask<IMRecvInfo> {

    @Override
    public void onMessage(IMRecvInfo recvInfo) {
        AbstractMessageProcessor processor = ProcessorFactory.createProcessor(IMCmdType.PRIVATE_MESSAGE);
        processor.process(recvInfo);
    }

}
