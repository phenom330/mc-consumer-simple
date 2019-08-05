package com.zb213.mc.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zb213.mc.consumer.pojo.MustParam;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


@Service
public class InvokeMessageCenterService {

    private String token;
    private long tokenExpiredTime;

    @Value("${consumer.queueKey}")
    private String queueKey;

    @Value("${consumer.queueSecret}")
    private String queueSecret;

    @Value("${consumer.queueDesc}")
    private String queueDesc;

    @Autowired
    private MustParam mustParam;

    @Value("${consumer.appKey}")
    private String appKey;

    @Value("${consumer.appSecret}")
    private String appSecret;

    @Value("${consumer.server.baseUrl}")
    private String serverBaseUrl;

    @Value("${consumer.server.queueRegisterEndpoint}")
    private String serverQueueRegisterEndpoint;

    @Value("${consumer.server.tokenEndpoint}")
    private String tokenEndpoint;

    @Value("${consumer.server.callBackEndpoint}")
    private String callBackEndpoint;

    Logger log = LoggerFactory.getLogger(InvokeMessageCenterService.class);

    public String queueRegister() throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("queueKey", this.queueKey);
        map.add("queueSecret", this.queueSecret);
        map.add("queueDesc", this.queueDesc);

        ObjectMapper mapper = new ObjectMapper();
        map.add("mustParamaters", mapper.writeValueAsString(this.mustParam.getParam()));


        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(map);
        String queueId = "";
        try {

            ResponseEntity<String> responseEntity = restTemplate.postForEntity(serverBaseUrl + serverQueueRegisterEndpoint, request, String.class);
            Map<String,Object> bodyMap = mapper.readValue(responseEntity.getBody(), HashMap.class);
            queueId = bodyMap.get("data") + "";
        }catch (HttpServerErrorException e) {

            log.error(e.getResponseBodyAsString());
            e.printStackTrace();
            throw e;
        }

        return queueId;
    }

    private HttpEntity<MultiValueMap<String, String>> getMultiValueMapHttpEntity(MultiValueMap<String, String> map) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.add("Accept", MediaType.APPLICATION_JSON_UTF8_VALUE);
        headers.add("X-Auth-Token", this.getToken());

        return new HttpEntity<MultiValueMap<String, String>>(map, headers);
    }

    public String getToken() throws IOException {

        return this.getToken(false);
    }

    public String getToken(boolean force) throws IOException {

        if(StringUtils.isNotBlank(token) && tokenExpiredTime > new Date().getTime() && !force){

            return token;
        }

        RestTemplate restTemplate = new RestTemplate();
        Map<String, String> map = new HashMap<String, String>();
        map.put("appKey", appKey);
        map.put("appSecret", appSecret);

        try {

            ResponseEntity<String> responseEntity = restTemplate.getForEntity(serverBaseUrl + tokenEndpoint + "?appKey={appKey}&appSecret={appSecret}", String.class, map);

            ObjectMapper mapper = new ObjectMapper();
            Map<String,Object> bodyMap = mapper.readValue(responseEntity.getBody(), HashMap.class);

            this.token = ((Map)bodyMap.get("data")).get("token") + "";
            this.tokenExpiredTime = Long.parseLong(((Map)bodyMap.get("data")).get("expire_time") + "");
        }catch (HttpServerErrorException e) {

            log.error(e.getResponseBodyAsString());
            e.printStackTrace();
            throw e;
        }

        return this.token;
    }

    public void sendStatusToMessageCenter(String msgId, String queueId, String useForRevField, String useForRevFieldValue, boolean success, String errorInfo, int errorCode, boolean onlyUpdateNoStatus) throws IOException {

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
        map.add("msgId", msgId);
        map.add("queueId", queueId);
        map.add("useForRevField", useForRevField);
        map.add("useForRevFieldValue", useForRevFieldValue);
        map.add("success", success + "");
        map.add("errorInfo", errorInfo);
        map.add("errorCode", errorCode + "");
        map.add("onlyUpdateNoStatus", onlyUpdateNoStatus + "");
        map.add("handleTime", new Date().getTime() + "");

        HttpEntity<MultiValueMap<String, String>> request = getMultiValueMapHttpEntity(map);
        try {

            restTemplate.postForEntity(serverBaseUrl + callBackEndpoint, request, String.class);
        }catch (HttpServerErrorException e) {

            log.error(e.getResponseBodyAsString());
            e.printStackTrace();
            throw e;
        }
    }

}
