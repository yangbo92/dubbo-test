package com.myself.dubbo.bean;

import lombok.Data;

/**
 * <Description>
 *
 * @author yangbo
 */
@Data
public class DubboServiceRequest {
    private String registryAddress;

    private String version = "1.0.0";

    private String group;

    private String clsAndMethodName;

    private String[] paramTypes;

    private Object[] businessParams;

}
