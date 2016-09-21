package com.ccit.ui;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

/**
 * 窗口操作工具类
 */
public class FrameTool {

    /**
     * 屏幕居中方法
     */
    public static void setCenter(JFrame jframe) {
        Toolkit kit = Toolkit.getDefaultToolkit();
        Dimension screenSize = kit.getScreenSize();
        int screenHeight = screenSize.height;
        int screenWidth = screenSize.width;
        jframe.setSize(screenWidth*2/3, screenHeight*2/3);
        int frameH = jframe.getHeight();
        int frameW = jframe.getWidth();
        jframe.setLocation((screenWidth - frameW) / 2, (screenHeight - frameH) / 2);
		try {
			String src = "images/logo.gif";
			File f = new File(src);
			Image image = ImageIO.read(f);
			jframe.setIconImage(image);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
