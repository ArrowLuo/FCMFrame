/**
 * 
 */
package com.ccit.main;

import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.util.Enumeration;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;
import javax.swing.plaf.FontUIResource;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import com.ccit.ui.FrameTool;
import com.ccit.ui.MainFrame;

/**
 * @author Huaishao Luo
 * @create 2016年9月14日下午12:46:46
 */
public class Main {
    public static void main(String[] args) {
        try {
            resetStyle();
            MainFrame mainFrame = new MainFrame();
            mainFrame.setResizable(true);
            Image icon = Toolkit.getDefaultToolkit().getImage("");
            mainFrame.setIconImage(icon);
            FrameTool.setCenter(mainFrame);
            mainFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            mainFrame.setDefaultCloseOperation(MainFrame.EXIT_ON_CLOSE);
            mainFrame.setTitle("FCM示例程序@Arrow_Luo");
            mainFrame.setVisible(true);
        } catch (Exception e) {
        	e.printStackTrace();
        	JOptionPane.showMessageDialog(null, "窗口启动异常", "提醒", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void resetStyle() throws Exception {
        BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.osLookAndFeelDecorated;
        BeautyEyeLNFHelper.launchBeautyEyeLNF();
        UIManager.put("RootPane.setupButtonVisible", false);
        UIManager.put("ToolBar.isPaintPlainBackground", Boolean.TRUE);
        initGlobalFontSetting(new Font("微软雅黑", Font.PLAIN, 14));
    }


    public static void initGlobalFontSetting(Font fnt) {
        FontUIResource fontRes = new FontUIResource(fnt);
        for (Enumeration<?> keys = UIManager.getDefaults().keys(); keys.hasMoreElements(); ) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof FontUIResource) {
            	UIManager.put(key, fontRes);
            }
        }
    }
}
