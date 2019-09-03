package framework;

import java.awt.*;

/**
 * @Author: leeping
 * @Date: 2019/8/9 13:08
 */
public class SwingUtil {
    private SwingUtil(){}

    public static boolean isDebug = true;

    public static Dimension getScreenSize(Window window) {
        Dimension screensize = Toolkit.getDefaultToolkit().getScreenSize();
        int w = screensize.width;
        int h = screensize.height;
        Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(
                window.getGraphicsConfiguration());
        w = w - (screenInsets.left + screenInsets.right);
        h = h - (screenInsets.top + screenInsets.bottom);
        return new Dimension(w, h);
    }
}
