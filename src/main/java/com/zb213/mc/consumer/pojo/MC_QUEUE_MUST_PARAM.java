package com.zb213.mc.consumer.pojo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

@JsonAutoDetect(fieldVisibility=JsonAutoDetect.Visibility.ANY, getterVisibility=JsonAutoDetect.Visibility.NONE)
public class MC_QUEUE_MUST_PARAM  implements Serializable {

    private static final long serialVersionUID = 1L;

    @Getter
    @Setter
    @JsonProperty
    private String ID;

    @Getter
    @Setter
    @JsonProperty
    private String QUEUE_ID;

    @Getter
    @Setter
    @JsonProperty
    private String TYPE_CODE;

    @Getter
    @Setter
    @JsonProperty
    private String FIELD;

    @Getter
    @Setter
    @JsonProperty
    private String DESCRIPTION;

    @Getter
    @Setter
    @JsonProperty
    private String USE_FOR_REV;

    @Getter
    @Setter
    @JsonProperty
    private String IS_ARRAY;

}
