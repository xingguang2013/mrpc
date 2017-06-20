package com.kongzhong.mrpc.client.proxy;

import com.google.common.reflect.AbstractInvocationHandler;
import com.kongzhong.mrpc.client.cluster.ha.HaStrategy;
import com.kongzhong.mrpc.client.cluster.loadblance.LoadBalance;
import com.kongzhong.mrpc.client.cluster.loadblance.SimpleLoadBalance;
import com.kongzhong.mrpc.config.ClientConfig;
import com.kongzhong.mrpc.model.RpcRequest;
import com.kongzhong.mrpc.utils.StringUtils;

import java.lang.reflect.Method;

/**
 * 默认的客户端代理
 *
 * @author biezhi
 *         2017/4/28
 */
public class SimpleClientProxy<T> extends AbstractInvocationHandler {

    // 负载均衡器
    protected LoadBalance loadBalance = new SimpleLoadBalance();

    // HA策略
    protected HaStrategy haStrategy = ClientConfig.me().getHaStrategy();

    @Override
    protected Object handleInvocation(Object proxy, Method method, Object[] args) throws Exception {
        RpcRequest request = RpcRequest.builder()
                .appId(ClientConfig.me().getAppId())
                .requestId(StringUtils.getUUID())
                .methodName(method.getName())
                .className(method.getDeclaringClass().getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args)
                .returnType(method.getReturnType())
                .build();

        return haStrategy.call(request, loadBalance);
    }

}