package framework.cef;



import bottle.util.Log4j;
import framework.SwingUtil;
import framework.jsbridge.IBridgeImp;
import framework.jsbridge.JSInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

/**
 * @Author: leeping
 * @Date: 2019/8/9 10:49
 */
public class CEFSwingWindow extends JFrame {

    private final static ArrayList<CEFSwingWindow> windowList = new ArrayList<>();

    private CEFHandler cefHandler;

    private final WindowAdapter windowsAdapter = new WindowAdapter() {

        @Override
        public void windowOpened(WindowEvent e) {
            super.windowOpened(e);
           if (SwingUtil.isDebug) Log4j.info("swing - windowOpened " + Thread.currentThread());
            //打开浏览器
            openBrowser();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowClosing " + Thread.currentThread());
        }

        @Override
        public void windowStateChanged(WindowEvent e) {
            super.windowStateChanged(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowStateChanged " + Thread.currentThread());
        }

        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowClosed " + Thread.currentThread());
            //关闭浏览器
            closeBrowser();
            removeWindowListener(windowsAdapter);
        }

        @Override
        public void windowIconified(WindowEvent e) {
            super.windowIconified(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowIconified " + Thread.currentThread());
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            super.windowDeiconified(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowDeiconified " + Thread.currentThread());
        }

        @Override
        public void windowActivated(WindowEvent e) {
            super.windowActivated(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowActivated " + Thread.currentThread());
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            super.windowDeactivated(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowDeactivated " + Thread.currentThread());
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {
            super.windowGainedFocus(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowGainedFocus" + Thread.currentThread());
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            super.windowLostFocus(e);
            if (SwingUtil.isDebug) Log4j.info("swing - windowLostFocus" + Thread.currentThread());
        }
    };

    private static IBridgeImp bridgeImp;

    public static void setJsBridge(IBridgeImp bridgeImp) {
        CEFSwingWindow.bridgeImp = bridgeImp;
    }

    public static void executeUrl(String target_url) {
        new CEFSwingWindow().execute(new CEFHandler(target_url,new JSInterface(bridgeImp)));
    }

    private void openBrowser() {
        if (cefHandler !=null) {
            this.add( cefHandler.stat(), BorderLayout.CENTER);
            this.setVisible(true);
            windowList.add(this);
        }
    }

    private void closeBrowser() {
        if (cefHandler !=null) {
            cefHandler.stop();
            cefHandler = null;
            windowList.remove(this);
        }
    }

    private CEFSwingWindow(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(windowsAdapter);
      setSize(SwingUtil.getScreenSize(this)); //屏幕大小
        if (SwingUtil.isDebug) setSize(600,400); //屏幕大小
//        setTitle("一块医药仓储ERP");
        setResizable(false);
    }

    public void execute(CEFHandler browser){
        this.cefHandler = browser;
        this.setVisible(true);
    }

}
