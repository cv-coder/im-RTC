package com.lwf.imclient.listener;

import com.lwf.imcommon.model.IMSendResult;

import java.util.List;

public interface MessageListener<T> {

     void process(List<IMSendResult<T>> result);

}
