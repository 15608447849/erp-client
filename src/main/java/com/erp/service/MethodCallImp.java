package com.erp.service;

/**
 * @Author: leeping
 * @Date: 2019/8/11 19:31
 * 提供给前端调用的本地方法
 */
public class MethodCallImp {
    private NativeServerImp nsi;
    public MethodCallImp(NativeServerImp nsi) {
        this.nsi = nsi;
    }

}
