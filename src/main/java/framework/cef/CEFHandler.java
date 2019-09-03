package framework.cef;

import bottle.util.Log4j;
import com.erp.service.NativeServerImp;
import framework.jsbridge.JSInterface;

import org.cef.CefApp;
import org.cef.CefClient;
import org.cef.CefSettings;
import org.cef.OS;
import org.cef.browser.CefBrowser;
import org.cef.browser.CefFrame;
import org.cef.callback.*;
import org.cef.handler.*;
import org.cef.misc.BoolRef;

import java.awt.*;
import java.io.File;
import java.lang.reflect.Field;
import java.net.URL;

/**
 * @Author: leeping
 * @Date: 2019/8/9 17:01
 * cef 实现的浏览器
 */
public class CEFHandler{

    static {
        boolean isLiunx = OS.isLinux();
        if (isLiunx) throw new RuntimeException("请运行在windows系统");
        dynamicDllLoad();
        initCefApp();
    }

    private static String  ROOT_PATH ;
    /* 加载库文件 */
    private static void dynamicDllLoad(){
        try{
            ROOT_PATH =  new File(CEFHandler.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getParent();
            //添加dll文件到系统目录
            URL url = CEFHandler.class.getResource("/cef");
            Log4j.info("URL = " + url);
            String fp;
            if (url == null) {
                fp = ROOT_PATH +"/resources/cef";
            }else{
                fp = url.getPath();
            }

            Log4j.info("fp = " + fp);

            fp += System.getProperty("os.arch").endsWith("64") ? "/win64" : "/win32";

            File file = new File(fp);
            if (!file.exists()) throw new RuntimeException("找不到内核库文件");

            Log4j.info("fp file = " + file);

            String syslib = System.getProperty("java.library.path");
            syslib = file.getCanonicalPath() + ";" + syslib;
            System.setProperty("java.library.path", syslib);
            Field fieldSysPath = ClassLoader.class.getDeclaredField("sys_paths");
            fieldSysPath.setAccessible(true);
            fieldSysPath.set(null, null);
        }catch (Exception e){
            throw new RuntimeException(e);
        }

    }

    //空白页
//    private static final String BLANK_URL = "about:blank";
    private static final String BLANK_URL = "http://www.baidu.com";

    private static CefApp cefApp;


    /* 初始化CEF APP 设置 */
    private static CefSettings initCefAppSetting() {
        Log4j.info("设置CEF APP,根目录 : " + ROOT_PATH);
        CefSettings settings = new CefSettings();
        settings.windowless_rendering_enabled = false;
        settings.ignore_certificate_errors = true;
        settings.cache_path = ROOT_PATH+"/store/cache";
        settings.log_file = ROOT_PATH+"/store/log";
//        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_VERBOSE;
        settings.log_severity = CefSettings.LogSeverity.LOGSEVERITY_INFO;
        settings.remote_debugging_port = 9999;
        settings.uncaught_exception_stack_size = 10;
        settings.persist_session_cookies = true;
        settings.command_line_args_disabled = true;
        return settings;
    }

    /*初始化cef app*/
    private static void initCefApp(){
            closeCefApp();
            CefApp.addAppHandler(
                    new CefAppHandler() {
                @Override
                public void onBeforeCommandLineProcessing(String s, CefCommandLine cefCommandLine) {
           // Log4j.info("CEF - onBeforeCommandLineProcessing - " + s +" , " +cefCommandLine.toString());
                }

                @Override
                public boolean onBeforeTerminate() {
                    Log4j.info("CEF - onBeforeTerminate");
                    return false;
                }

                @Override
                public void stateHasChanged(org.cef.CefApp.CefAppState state) {
                    Log4j.info(Thread.currentThread()+ " CEF - stateHasChanged - " + state );
                }

                @Override
                public void onRegisterCustomSchemes(CefSchemeRegistrar cefSchemeRegistrar) {
                    Log4j.info("CEF - onRegisterCustomSchemes - "+cefSchemeRegistrar);
                }

                @Override
                public void onContextInitialized() {
                    Log4j.info("CEF - onContextInitialized");
                }

                @Override
                public CefPrintHandler getPrintHandler() {
                    Log4j.info("CEF - getPrintHandler");
                    return null;
                }

                @Override
                public void onScheduleMessagePumpWork(long l) {
                    Log4j.info("CEF - onScheduleMessagePumpWork - "+ l);
                }
            });
        cefApp = CefApp.getInstance( initCefAppSetting());
        CefApp.CefVersion version = cefApp.getVersion();
        Log4j.info("当前CEF内核版本信息:\t" + version.toString().replaceAll("\n"," ,"));
    }

    /* 结束cef - app */
    public static void closeCefApp(){
        if (cefApp == null) return;
        cefApp.dispose();
        cefApp = null;
    }

    //浏览器客户端
    private CefClient client;
    //浏览器
    private CefBrowser browser;
    //打开的连接地址
    private String startURL;
    //js-java交互
    private JSInterface jsBridge ;

    CEFHandler(String startURL,JSInterface jsBridge) {
        this.jsBridge = jsBridge;
        this.startURL = startURL;
        bindJsBridge();
        client = cefApp.createClient();
        initClient();
        browser = client.createBrowser(startURL,false,false);
    }

    private void bindJsBridge() {
        if (jsBridge!=null){
            jsBridge.setCallJsSender(js -> {
                if (browser!=null) browser.executeJavaScript(js,null,0);
            });
        }
    }

    //打开浏览器
    Component stat(){
        return browser.getUIComponent();

    }

    // 初始化客户端信息
    private void initClient() {
        client.addLoadHandler(new CefLoadHandlerAdapter() {

            @Override
            public void onLoadError(CefBrowser cefBrowser, CefFrame cefFrame, ErrorCode errorCode, String s, String s1) {
                Log4j.info(Thread.currentThread() + " 加载错误 " + cefBrowser.getURL() +" "+errorCode);
                if (errorCode == ErrorCode.ERR_ABORTED || errorCode == ErrorCode.ERR_NONE) return;
                cefBrowser.reload();
            }
        });

        client.addJSDialogHandler(new CefJSDialogHandlerAdapter() {


            @Override
            public boolean onJSDialog(CefBrowser cefBrowser, String origin_url, JSDialogType jsDialogType, String message_text, String default_prompt_text, CefJSDialogCallback cefJSDialogCallback, BoolRef boolRef) {
                if (jsBridge != null){
                    if (jsBridge.handle(message_text)){
                        Log4j.info(Thread.currentThread() + " 弹窗内容: "+ message_text);
                        cefJSDialogCallback.Continue(false,null);
                        return true;
                    }
                }
               return super.onJSDialog(cefBrowser, origin_url, jsDialogType, message_text, default_prompt_text, cefJSDialogCallback, boolRef);
            }
        });

        client.addDisplayHandler(new CefDisplayHandlerAdapter() {

            @Override
            public boolean onConsoleMessage(CefBrowser cefBrowser, CefSettings.LogSeverity logSeverity,String message, String source, int line) {
//                Log4j.info(Thread.currentThread()  + "控制台日志打印:\n"+ message);
                return true;
//                return super.onConsoleMessage(cefBrowser, message, source, line);
            }
        });

        client.addKeyboardHandler(new CefKeyboardHandlerAdapter() {
            @Override
            public boolean onKeyEvent(CefBrowser cefBrowser, CefKeyEvent cefKeyEvent) {
//                Log4j.info(Thread.currentThread() + "键盘点击: " + cefKeyEvent);
                return super.onKeyEvent(cefBrowser, cefKeyEvent);
            }
        });

        client.addDownloadHandler(new CefDownloadHandlerAdapter() {
            @Override
            public void onBeforeDownload(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, String suggestedName, CefBeforeDownloadCallback cefBeforeDownloadCallback) {
                Log4j.info("文件下载: " + cefDownloadItem+" - "+ suggestedName+" - "+cefBeforeDownloadCallback);
//                super.onBeforeDownload(cefBrowser, cefDownloadItem, suggestedName, cefBeforeDownloadCallback);
            }

            @Override
            public void onDownloadUpdated(CefBrowser cefBrowser, CefDownloadItem cefDownloadItem, CefDownloadItemCallback cefDownloadItemCallback) {
                Log4j.info("文件下载进度更新: " + cefDownloadItem+" - "+ cefDownloadItemCallback);
                super.onDownloadUpdated(cefBrowser, cefDownloadItem, cefDownloadItemCallback);
            }
        });

        client.addContextMenuHandler(new CefContextMenuHandlerAdapter() {


            @Override
            public void onBeforeContextMenu(CefBrowser cefBrowser,CefFrame cefFrame, CefContextMenuParams cefContextMenuParams, CefMenuModel cefMenuModel) {
                cefMenuModel.clear(); //去除菜单栏
            }
        });

        client.addLifeSpanHandler(new CefLifeSpanHandlerAdapter() {
            @Override
            public boolean onBeforePopup(CefBrowser cefBrowser, CefFrame cefFrame, String target_url, String target_frame_name) {
                Log4j.info("onBeforePopup  target_url: " + target_url +" , target_frame_name: " + target_frame_name);
                CEFSwingWindow.executeUrl(target_url); //打开新窗口
                return true;
//                return super.onBeforePopup(cefBrowser, cefFrame, target_url, target_frame_name);
            }
        });
    }

    //停止浏览器
    void stop(){
        if (jsBridge != null){
            jsBridge.close();
            jsBridge = null;
        }
        if (browser!=null && client!=null){
            browser.getUIComponent().getParent().remove(browser.getUIComponent());
            browser.stopLoad();
            browser.onBeforeClose();
            browser = null;
            client.dispose();
            client = null;
        }
    }

}
