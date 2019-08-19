package framework.cef;



import bottle.util.Log4j;
import framework.SwingUtil;
import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * @Author: leeping
 * @Date: 2019/8/9 10:49
 */
public class CEFWindow extends JFrame {

    private CEFHandler browser;

    private WindowAdapter windowsAdapter = new WindowAdapter() {
        @Override
        public void windowOpened(WindowEvent e) {
            super.windowOpened(e);
            Log4j.info("windowOpened " + Thread.currentThread());
            //打开浏览器
            openBrowser();
        }

        @Override
        public void windowClosing(WindowEvent e) {
            super.windowClosing(e);
            Log4j.info("windowClosing " + Thread.currentThread());
        }

        @Override
        public void windowStateChanged(WindowEvent e) {
            super.windowStateChanged(e);
            Log4j.info("windowStateChanged " + Thread.currentThread());
        }

        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            Log4j.info("windowClosed " + Thread.currentThread());
            //关闭浏览器
            closeBrowser();
            removeWindowListener(windowsAdapter);
        }

        @Override
        public void windowIconified(WindowEvent e) {
            super.windowIconified(e);
            Log4j.info("WindowEvent " + Thread.currentThread());
        }

        @Override
        public void windowDeiconified(WindowEvent e) {
            super.windowDeiconified(e);
            Log4j.info("windowDeiconified " + Thread.currentThread());
        }

        @Override
        public void windowActivated(WindowEvent e) {
            super.windowActivated(e);
            Log4j.info("windowActivated " + Thread.currentThread());
        }

        @Override
        public void windowDeactivated(WindowEvent e) {
            super.windowDeactivated(e);
            Log4j.info("windowDeactivated " + Thread.currentThread());
        }

        @Override
        public void windowGainedFocus(WindowEvent e) {
            super.windowGainedFocus(e);
            Log4j.info("windowGainedFocus" + Thread.currentThread());
        }

        @Override
        public void windowLostFocus(WindowEvent e) {
            super.windowLostFocus(e);
            Log4j.info("windowLostFocus" + Thread.currentThread());
        }
    };

    private void openBrowser() {
        if (browser!=null) {
            this.add(browser.stat(), BorderLayout.CENTER);
            this.setVisible(true);
//            this.repaint();
        }
    }

    private void closeBrowser() {
        if (browser!=null) {
            browser.stop();
            browser = null;
        }
    }

    private CEFWindow(){
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        addWindowListener(windowsAdapter);
        setSize(SwingUtil.getScreenSize(this)); //屏幕大小
//        setSize(1200,400); //屏幕大小
//        setTitle("一块医药仓储ERP");
        setResizable(false);
        setLayout(new BorderLayout(10,5));
        addProgress();
    }

    private void addProgress() {

        JPanel panelTop = new JPanel(new BorderLayout());
        this.add(panelTop,BorderLayout.SOUTH);
        JTextArea textArea = new JTextArea(10,15);
        textArea.setTabSize(4);
        textArea.setFont(new Font("标楷体", Font.BOLD, 16));
        textArea.setBackground(Color.pink);

        JScrollPane jscrollPane = new JScrollPane(textArea);
        panelTop.add(jscrollPane);

        Log4j.setCallback(o -> {
            textArea.append(o.toString());
        });
    }

    private static final CEFWindow INSTANCE = new CEFWindow();

    public static void execute(CEFHandler browser){
        INSTANCE.browser = browser;
        INSTANCE.setVisible(true);
    }

}
