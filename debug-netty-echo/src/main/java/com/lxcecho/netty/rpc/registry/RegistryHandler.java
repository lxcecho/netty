package com.lxcecho.netty.rpc.registry;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import com.lxcecho.netty.rpc.protocol.InvokerProtocol;

import java.io.File;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 实现注册具体逻辑
 *
 * @author lxcecho lxcecho@gmail.com
 * @since 16:29 29-10-2022
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    /**
     * 保存所有可用的服务
     */
    private static final ConcurrentHashMap<String, Object> registryMap = new ConcurrentHashMap<>();

    private final List<String> classNames = new ArrayList<>();

    public RegistryHandler() {
        // finish recursive scanning
        scannerClass("io.netty.rpc.provider");
        doRegister();
    }

    /**
     * 递归扫描
     *
     * @param packageName
     */
    private void scannerClass(String packageName) {
        URL url = this.getClass().getClassLoader().getResource(packageName.replaceAll("\\.", "/"));
        assert url != null;
        File dir = new File(url.getFile());
        for (File file : Objects.requireNonNull(dir.listFiles())) {
            // 如果是一个文件夹，继续递归
            if (file.isDirectory()) {
                scannerClass(packageName + "." + file.getName());
            } else {
                classNames.add(packageName + "." + file.getName().replace(".class", "").trim());
            }
        }
    }

    /**
     * 完成注册
     */
    private void doRegister() {
        if (classNames.isEmpty()) {
            return;
        }
        for (String className : classNames) {
            try {
                Class<?> clazz = Class.forName(className);
                Class<?> in = clazz.getInterfaces()[0];
                registryMap.put(in.getName(), clazz.newInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取客户端发送消息，并调用服务
     *
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        Object result = new Object();
        InvokerProtocol request = (InvokerProtocol) msg;
        // 当客户端建立连接时，需要从自定义协议中获取信息，以及具体的服务和实参，使用反射调用
        if (registryMap.containsKey(request.getClassName())) {
            Object clazz = registryMap.get(request.getClassName());
            Method method = clazz.getClass().getMethod(request.getMethodName(), request.getParams());
            result = method.invoke(clazz, request.getValues());
        }
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
