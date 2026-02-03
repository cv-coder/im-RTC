package com.lwf.imserver.netty;

public interface IMServer {

    boolean isReady();

    void start();

    void stop();
}
