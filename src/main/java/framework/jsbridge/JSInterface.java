package framework.jsbridge;



import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import bottle.threadpool.IOThreadPool;
import bottle.util.GsonUtils;


/**
 * js / native 通讯接口
 */
public class JSInterface implements IJsBridge {

    private static final StringBuilder native_js_content = new StringBuilder();

    static {
        initNativeInterfaceJs("_native_core.js");
        initNativeInterfaceJs("_native_bridge.js");
    }

    private static void initNativeInterfaceJs(String jsFile) {
        try(InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(jsFile);){
            if (is!=null){
                byte[] bytes = new byte[100];
                int len;
                while ( (len = is.read(bytes) ) > 0 ){
                    native_js_content.append(new String(bytes,0,len));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String JsFile() {
        return native_js_content.toString();
    }

    public  interface CallJsSender{
        void executeJs(String js);
    }

    private final static String JAVA_SCRIPT = "javascript:";

    private final static String JS_INTERFACE_INVOKE_NAME = JAVA_SCRIPT + "jsBridge._invoke('%s','%s','%s')";// invoke js function

    private final static String JS_INTERFACE_NAME = JAVA_SCRIPT + "jsBridge._callbackInvoke('%s','%s')";// js callback function

    private IBridgeImp hImp;

    private CallJsSender sender;

    public JSInterface() { }

    public JSInterface(IBridgeImp hImp) {
        setIBridgeImp(hImp);
    }

    public void setCallJsSender(CallJsSender sender) {
        this.sender = sender;
    }

    //关联一个实现
    public IJsBridge setIBridgeImp(IBridgeImp imp){
        this.hImp = imp;
        this.hImp.setIJsBridge(this);
        return this;
    }

    private final HashMap<String, JSCallback> jsCallbackMap = new HashMap<>();

    private static IOThreadPool pool = new IOThreadPool();

    /**
     * 将奇数个转义字符变为偶数个
     */
    private static String getDecodeJSONStr(String s){
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0; i < s.length(); i++) {
            c = s.charAt(i);
            switch (c) {
                case '\\':
                    sb.append("\\\\");
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    public boolean handle(String data){
        InteractionProtocol.ProtocolBody body = InteractionProtocol.pause(data);
        if (body!=null){
            handle(body);
            return true;
        }
        return false;
    }


    private void handle(InteractionProtocol.ProtocolBody body) {

        pool.post(() -> {
            if (body.mark.equals("invoke")){
                invoke(body.method,body.param,body.callbackId);
            }
            if (body.mark.equals("callbackInvoke")){
                callbackInvoke(body.callbackId,body.param);
            }
        });

    }

    private void  invoke(String method,String param,String callbackId){

        Object value = null;
        try {
            if (hImp!=null){
                value = hImp.invoke(method,param);
            }
        } catch (Exception e) {
            e.printStackTrace();
            value = "bridge execute error:\t"+ e;
        }
        if (callbackId == null) return;

        final String result  = value == null ? null :
                value instanceof String ? getDecodeJSONStr(value.toString()) : GsonUtils.javaBeanToJson(value);
        executeJs(String.format(JS_INTERFACE_NAME,callbackId ,result));
    }

    // 主动调用js方法
    @Override
    public void requestJs(final String method, final String data, JSCallback callback){
        String callbackId = null;
        if (callback!=null){
            callbackId = "java_callback_"+System.currentTimeMillis();
            jsCallbackMap.put(callbackId,callback);
        }
        final String _callbackId = callbackId;
        executeJs(String.format(JS_INTERFACE_INVOKE_NAME,method ,data,_callbackId));
    }

    public void executeJs(String content) {
        try {
            if (sender!=null) sender.executeJs(content);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * native -> js ,js回调
     */
    private void callbackInvoke(String callback_id,String data){
        try {

            JSCallback callback = jsCallbackMap.remove(callback_id);
            if (callback==null) throw new Exception(callback_id + " callback function doesn\'t exist!");
            callback.callback(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    public void close(){
        pool.close();

        if (this.sender!=null){
            this.sender = null;
        }

        if (this.hImp!=null) {
            this.hImp.close();
            this.hImp = null;
        }
    }

}
