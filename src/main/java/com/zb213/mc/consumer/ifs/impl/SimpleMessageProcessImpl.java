package com.zb213.mc.consumer.ifs.impl;

import com.zb213.mc.consumer.ifs.IMessageProcess;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SimpleMessageProcessImpl implements IMessageProcess {
    @Override
    public void sendMsg(List<String> sendList, Map<String, Object> msgBodyMap) throws Exception {

    }
}
