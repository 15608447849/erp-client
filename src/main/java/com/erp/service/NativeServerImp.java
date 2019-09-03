package com.erp.service;

import bottle.util.GsonUtils;
import framework.jsbridge.IBridgeImp;
import com.onek.client.IceClient;

import java.lang.reflect.Method;
import java.util.UUID;

/**
 * @Author: leeping
 * @Date: 2019/8/11 19:05
 */
public class NativeServerImp extends IBridgeImp {

    public final MethodCallImp caller;

    private NativeServerImp() {
        caller = new MethodCallImp(this);//伴生类
    }

    private static final NativeServerImp INSTANCE = new NativeServerImp();

    public static NativeServerImp getInstance(){ return INSTANCE;};

    private static IceClient ic = null;

    private static String DEVID = UUID.randomUUID().toString();

    public static NativeServerImp create(IceClient client){
        initServerCommunication(client);
        return getInstance();
    }

    //设置,连接服务器
    private static void initServerCommunication(IceClient client) {
        launchICE(client);
    }

    private static void destroyICE() {
        if (ic != null) {
            ic.stopCommunication();
        }
    }

    private static void launchICE(IceClient client) {
        destroyICE();
        ic = client.startCommunication();
    }


    //转发
    private String transfer(String serverName, String cls, String method,int page,int count,String json) {
        IceClient client =
                NativeServerImp.ic.settingProxy(serverName).
                        settingReq(DEVID,cls,method).
                        setPageInfo(page,count);
        if (json!=null){
            String[] arrays = null;
            if (GsonUtils.checkJsonIsArray(json)) arrays = GsonUtils.jsonToJavaBean(json,String[].class);
            if (arrays != null) client.settingParam(arrays);
            else client.settingParam(json);
        }
        return client.execute();
    }

    @Override
    public Object invoke(String methodName, String data) throws Exception {
        if (methodName.startsWith("ts:")){
            //转发协议  ts:服务名@类名@方法名@分页页码@分页条数
            String temp = methodName.replace("ts:","");
            String[] args = temp.split("@");
            return transfer(args[0],args[1],args[2],Integer.parseInt(args[3]),Integer.parseInt(args[4]),data);
        }
        Object val;
        //反射调用方法
        if(data == null){
            Method m = caller.getClass().getDeclaredMethod(methodName);
            m.setAccessible(true);
            val =  m.invoke(caller);
        }else{
            Method m = caller.getClass().getDeclaredMethod(methodName,String.class);
            m.setAccessible(true);
            val = m.invoke(caller,data);
        }
        return val;
    }

    @Override
    protected void close() {
        destroyICE();
    }
}
