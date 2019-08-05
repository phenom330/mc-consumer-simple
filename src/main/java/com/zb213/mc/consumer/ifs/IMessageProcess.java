package com.zb213.mc.consumer.ifs;

import java.util.List;
import java.util.Map;

public interface IMessageProcess {

    void sendMsg(List<String> sendList, Map<String, Object> msgBodyMap) throws Exception;
}
