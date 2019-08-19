package framework.jsbridge;

import bottle.util.GsonUtils;

import java.util.Arrays;

/**
 * @Author: leeping
 * @Date: 2019/8/11 17:17
 */
public class InteractionProtocol {

    // 例如:    bridge:json协议体
    public static final String PROTOCOL_HEADER = "bridge:";

    public static class ProtocolBody{

        public String mark ;

        public String method;

        public String param;

        public String callbackId;

        @Override
        public String toString() {
            return "ProtocolBody{" +
                    "mark='" + mark + '\'' +
                    ", method='" + method + '\'' +
                    ", param='" + param + '\'' +
                    ", callbackId='" + callbackId + '\'' +
                    '}';
        }
    }

    public static ProtocolBody pause(String data){
        if (!data.startsWith(PROTOCOL_HEADER)) return null;
        String json = data.trim().replace(PROTOCOL_HEADER,"");
        return GsonUtils.jsonToJavaBean(json,ProtocolBody.class);
    }


    public static void main(String[] args) {

        ProtocolBody protocolBody = new ProtocolBody();
        protocolBody.mark = "java";
        protocolBody.method = "invoke";
        protocolBody.param = "[ \"Google\", \"Runoob\", \"Taobao\" ]";
        protocolBody.callbackId = "js_callback_20190811_100";
        String dara = PROTOCOL_HEADER+GsonUtils.javaBeanToJson(protocolBody);


        protocolBody = pause(dara);
        System.out.println(protocolBody);
        String[] o = GsonUtils.jsonToJavaBean(protocolBody.param,String[].class);
        System.out.println(Arrays.toString(o));


    }

}
