package com.zhwxp.sample.spring.boot.tcp.client.service.impl;

import java.time.LocalDateTime;

import com.zhwxp.sample.spring.boot.tcp.client.gateway.TcpClientGateway;
import com.zhwxp.sample.spring.boot.tcp.client.service.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageServiceImpl implements MessageService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MessageServiceImpl.class);

    private TcpClientGateway tcpClientGateway;

    @Autowired
    public MessageServiceImpl(TcpClientGateway tcpClientGateway) {
        this.tcpClientGateway = tcpClientGateway;
    }

    @Override
    public void sendMessage() {
        String message = "M1 A00190923010506420103200039140061765";
        LOGGER.info("Send message: {}", message);
        String response = tcpClientGateway.send(message);
        LOGGER.info("Receive response: {}", response);
    }

}
