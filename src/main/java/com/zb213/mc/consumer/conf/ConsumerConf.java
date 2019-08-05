package com.zb213.mc.consumer.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zb213.mc.consumer.common.SendMsgException;
import com.zb213.mc.consumer.ifs.IMessageProcess;
import com.zb213.mc.consumer.service.InvokeMessageCenterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
public class ConsumerConf {

    @Autowired
    private InvokeMessageCenterService messageCenterService;

    private String queueId;

    //数据包中标识用于接收消息的字段
    private final static String USE_FOR_REV_FIELD = "userIds";

    @Autowired
    private IMessageProcess process;

    Logger log = LoggerFactory.getLogger(ConsumerConf.class);

    @Bean
    public SimpleMessageListenerContainer messageListenerContainer(ConnectionFactory connectionFactory) throws IOException {

        this.queueId = messageCenterService.queueRegister();

        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(this.queueId);
        container.setMessageListener(message -> {
            log.info("====接收到" + message.getMessageProperties().getConsumerQueue() + "队列的消息=====");
            log.info(message.getMessageProperties() + "");
            log.info(new String(message.getBody()));

            ObjectMapper mapper = new ObjectMapper();

            try {

                Map<String, Object> bodyMap = mapper.readValue(new String(message.getBody()), HashMap.class);
                String msgId = bodyMap.get("_msgId") + "";

                try {
                    process.sendMsg((List<String>)bodyMap.get(ConsumerConf.USE_FOR_REV_FIELD), bodyMap);

                    messageCenterService.sendStatusToMessageCenter(msgId, queueId, "", "", true, "", 0, true);
                } catch (Exception e) {

                    e.printStackTrace();
                    if (e instanceof SendMsgException) {

                        messageCenterService.sendStatusToMessageCenter(msgId, queueId, "", "", false, e.getMessage(), ((SendMsgException) e).getErrorCode(), true);
                    } else {

                        messageCenterService.sendStatusToMessageCenter(msgId, queueId, "", "", false, e.getMessage(), 102, true);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return container;
    }


}
