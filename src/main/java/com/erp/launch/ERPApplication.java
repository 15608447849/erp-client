package com.erp.launch;
import bottle.util.Log4j;
import com.erp.service.NativeServerImp;
import com.onek.client.IceClient;
import framework.cef.CEFHandler;
import framework.cef.CEFSwingWindow;
import framework.jsbridge.JSInterface;
import properties.abs.ApplicationPropertiesBase;
import properties.annotations.PropertiesFilePath;
import properties.annotations.PropertiesName;

import static javafx.application.Application.launch;

/**
 * @Author: leeping
 * @Date: 2019/8/9 10:47
 */
@PropertiesFilePath("/app.properties")
public class ERPApplication extends ApplicationPropertiesBase {

    @PropertiesName("web.url")
    private static String URL;
    @PropertiesName("server.instance")
    private static String iceGridTag = "DemoIceGrid";
    @PropertiesName("server.address")
    private static String iceRegAddress = "114.116.155.221:4061";
    @PropertiesName("server.args")
    private static String iceArgs = "idleTimeOutSeconds=300,--Ice.MessageSizeMax=4096";

    public static void main(String[] args) {
        ApplicationPropertiesBase.initStaticFields(ERPApplication.class);
        Log4j.info(URL);
        CEFSwingWindow.setJsBridge( NativeServerImp.create(new IceClient(iceGridTag,iceRegAddress,iceArgs)));
        CEFSwingWindow.executeUrl(URL);
    }

}
