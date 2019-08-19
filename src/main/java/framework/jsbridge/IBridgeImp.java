package framework.jsbridge;


/**
 * Created by Leeping on 2019/6/3.
 * email: 793065165@qq.com
 */
public abstract class IBridgeImp{
    protected IJsBridge bridge;
    void setIJsBridge(IJsBridge bridge){
        this.bridge = bridge;
    }
    protected abstract Object invoke(final String methodName, final String data) throws Exception;

    protected void close(){

    }
}
