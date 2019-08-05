package com.zb213.mc.consumer.pojo;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties("consumer.must-paramaters")
public class MustParam {

    private List<MC_QUEUE_MUST_PARAM> param;

    public List<MC_QUEUE_MUST_PARAM> getParam() {
        return param;
    }

    public void setParam(List<MC_QUEUE_MUST_PARAM> param) {
        this.param = param;
    }
}
