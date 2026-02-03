package com.lwf.imclient;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@ComponentScan(basePackages = { "com.lwf.imclient", "com.lwf.imcommon" })
public class IMAutoConfiguration {

}
