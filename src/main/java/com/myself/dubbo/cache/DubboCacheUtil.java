package com.myself.dubbo.cache;

import java.util.Objects;

import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.utils.ReferenceConfigCache;
import com.alibaba.dubbo.rpc.service.GenericService;

/**
 * <Description>
 *
 * @author yangbo
 */
public class DubboCacheUtil {

    private static final String CUSTOM_NAME = "_CUSTOM_";

    private static final ReferenceConfigCache.KeyGenerator CUSTOM_KEY_GENERATOR = referenceConfig ->
            referenceConfig.getRegistry().getAddress() + "_" + ReferenceConfigCache.DEFAULT_KEY_GENERATOR.generateKey(referenceConfig);

    public static GenericService getGenericService(ReferenceConfig<GenericService> referenceConfig) {
        ReferenceConfigCache cache = ReferenceConfigCache.getCache(CUSTOM_NAME, CUSTOM_KEY_GENERATOR);
        GenericService genericService = cache.get(referenceConfig);
        // 初始化的时候提供者还没暴露服务，需要清缓存重试
        if (Objects.isNull(genericService)) {
            cache.destroy(referenceConfig);
            genericService = cache.get(referenceConfig);
        }
        return genericService;
    }
}
