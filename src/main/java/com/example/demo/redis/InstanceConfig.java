package com.example.demo.redis;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p>
 *      初始化实例，保证实例在系统内的唯一性
 * </p>
 *
 * @author 曾凯
 * @Version: V1.0
 * @since
 */
@Configuration
public class InstanceConfig {

    private static final ConcurrentHashMap<String, String> MAP = new ConcurrentHashMap<>(1);

    private static final String INSTANCE_ID = "instanceId";

    @PostConstruct
    public void initInstanceId(){
        MAP.put(INSTANCE_ID, UUID.randomUUID().toString());
    }

    public static String getInstanceId(){
        return MAP.get(INSTANCE_ID);
    }
}