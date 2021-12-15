package com.myself.dubbo.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.rpc.service.GenericService;
import com.myself.dubbo.bean.DubboServiceRequest;
import com.myself.dubbo.cache.DubboCacheUtil;

/**
 * <Description>
 *
 * @author yangbo
 */
@RestController
public class DubboServiceController {

    @Autowired
    private ApplicationConfig applicationConfig;

    @Autowired
    private RegistryConfig defaultRegistryConfig;

    @RequestMapping("/dubbo/test")
    public Object dubboServiceTest(@RequestBody DubboServiceRequest request) {
        ReferenceConfig<GenericService> referenceConfig = new ReferenceConfig();
        referenceConfig.setApplication(applicationConfig);
        referenceConfig.setRegistry(getRegistryConfig(request));
        referenceConfig.setGroup(request.getGroup());
        referenceConfig.setVersion(request.getVersion());
        String[] clsAndMethodArr = request.getClsAndMethodName().split("#");
        referenceConfig.setInterface(clsAndMethodArr[0]);
        referenceConfig.setGeneric(true);

        GenericService genericService = DubboCacheUtil.getGenericService(referenceConfig);
        return genericService.$invoke(clsAndMethodArr[1], request.getParamTypes(), request.getBusinessParams());
    }

    private RegistryConfig getRegistryConfig(@RequestBody DubboServiceRequest request) {
        RegistryConfig registryConfig = defaultRegistryConfig;
        if (StringUtils.isNotEmpty(request.getRegistryAddress())) {
            registryConfig = new RegistryConfig();
            registryConfig.setProtocol(DubboConfig.DEFAULT_REGISTRY_PROTOCOL);
            registryConfig.setAddress(request.getRegistryAddress());
        }
        return registryConfig;
    }

    @Configuration
    static class DubboConfig {

        public static final String DEFAULT_REGISTRY_PROTOCOL = "zookeeper";

        @Bean
        public ApplicationConfig applicationConfig(@Value("${dubbo.application.name}") String applicationName) {
            ApplicationConfig applicationConfig = new ApplicationConfig();
            applicationConfig.setName(applicationName);
            return applicationConfig;
        }

        @Bean
        public RegistryConfig registryConfig(@Value("${dubbo.registry.address}") String address) {
            RegistryConfig registryConfig = new RegistryConfig();
            registryConfig.setProtocol(DEFAULT_REGISTRY_PROTOCOL);
            registryConfig.setAddress(address);
            return registryConfig;
        }
    }

}


