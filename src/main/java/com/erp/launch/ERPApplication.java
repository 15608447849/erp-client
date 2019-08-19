package com.erp.launch;
import com.erp.service.NativeServerImp;
import framework.cef.CEFHandler;
import framework.cef.CEFWindow;
import framework.jsbridge.JSInterface;

import static javafx.application.Application.launch;

/**
 * @Author: leeping
 * @Date: 2019/8/9 10:47
 */
public class ERPApplication {

    public static void main(String[] args) {
//        String url = "https://ykdrugs.com";
//        String url = "http://114.116.149.145:9999/dist/index.html#/home";
        String url = "file://C:/IDEAWORK/erp-client/src/main/resources/dist/index.html";
        JSInterface jsInterface = null;
        jsInterface = new JSInterface();
        jsInterface.setIBridgeImp(NativeServerImp.create());
        CEFHandler imp = new CEFHandler(url,jsInterface);
        CEFWindow.execute(imp);
    }

}
