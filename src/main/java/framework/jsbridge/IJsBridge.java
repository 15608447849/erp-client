package framework.jsbridge;

/**
 * Created by Leeping on 2019/5/5.
 * email: 793065165@qq.com
 */
public interface IJsBridge {
    interface JSCallback{
        void callback(String data);
    }
    //主动调用js方法
    void requestJs(final String method, final String data, JSCallback callback);

}
