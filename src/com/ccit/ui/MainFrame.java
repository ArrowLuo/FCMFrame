package com.ccit.ui;


import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

import org.jb2011.lnf.beautyeye.ch3_button.BEButtonUI;
import org.jb2011.lnf.beautyeye.widget.N9ComponentFactory;

import cartesian.coordinate.CCPoint;
import cartesian.coordinate.CCPolygon;
import cartesian.coordinate.CCSystem;

import com.ccit.bean.CenterRooter;
import com.ccit.main.FCMAlgorithm;
import com.ccit.util.CSVFileUtil;
import com.ccit.util.CommUtils;
import com.ccit.util.ExportDialog;

/**
 */
public class MainFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final String ENCODE = "UTF-8";
	private String OLD_FILEPATH = "./files/";
	private String SOURCE_FILEPATH = "files";
	
	private static final int MAX_CLUSTERNUM = 21;		// 最大分类数
	private static final int MAX_ITERNUM = 100;			// 最大迭代次数
	private static final int MAX_EXPONENT = 6;			// 最大指数
	private static final int MAX_SPEED = 6;				// 最大显示速度等级
	
	private int clusternum = 3;							// 分类数
	private int iternum = 50;							// 迭代次数
	private int exponent = 3;							// 指数
	private int speed = 6;								// 显示速度
	private boolean flag_speed = true;					// 是否显示过程；默认显示
	
	JPanel innerPanel = new JPanel();
	JSlider s_clusternum = new JSlider(JSlider.VERTICAL, 1, MAX_CLUSTERNUM, clusternum);
	JSlider s_iternum = new JSlider(JSlider.VERTICAL, 1, MAX_ITERNUM, iternum);
	JSlider s_exponent = new JSlider(2, MAX_EXPONENT, exponent);
	JSlider s_speed = new JSlider(1, MAX_SPEED, speed);
	
	JLabel l_clusternum = N9ComponentFactory.createLabel_style1("分类数:"+clusternum);
	JLabel l_iternum = N9ComponentFactory.createLabel_style1("迭代次数:"+iternum);
	JLabel l_exponent = N9ComponentFactory.createLabel_style1("指数:"+exponent);
	JLabel l_speed = N9ComponentFactory.createLabel_style1("程序执行速度:"+speed);
	
	JButton startbtn = new JButton("  开始  ");				// 开始聚类
	JButton stopbtn = new JButton("  终止  ");				// 结束聚类
	
	CCSystem imagePanel = new CCSystem(0.0, 0.0, 10.0, 10.0);
	int SHOW_COLUMN_X = 0;									// 显示行坐标；默认数据第1列
	int SHOW_COLUMN_Y = 1;									// 显示行坐标；默认数据第2列
	
	List<double[]> datas = new ArrayList<double[]>();
	List<Integer> datas_label = new ArrayList<Integer>();
	
	List<Color> color_lists = new ArrayList<Color>();		// 最大颜色个数与最大分类数相等
	CenterRooter[] center_rooters = new CenterRooter[clusternum];	// 中心点移动轨迹
	
	private volatile boolean shutdownRequested = false;		// 终止标志
	Thread paintPointThread = null;
	Thread processThread = null;

    @SuppressWarnings("unchecked")
	public MainFrame() {
    	JMenuBar menuBar = createMenus();
    	setJMenuBar(menuBar);
    	
    	JPanel demoPanel = new JPanel();
    	demoPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
    	demoPanel.setLayout(new BorderLayout());
    	
		demoPanel.setLayout(new BoxLayout(demoPanel, BoxLayout.Y_AXIS));
		
		innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.X_AXIS));

		demoPanel.add(Box.createRigidArea(new Dimension(1,10)));
		demoPanel.add(innerPanel);
		demoPanel.add(Box.createRigidArea(new Dimension(1,10)));

		innerPanel.add(Box.createRigidArea(new Dimension(20,1)));
		
		// Create a panel to hold buttons
		JPanel buttonPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			public Dimension getMaximumSize() {
				return new Dimension(getPreferredSize().width, super.getMaximumSize().height);
			}
		};
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
		buttonPanel.add(Box.createRigidArea(new Dimension(1,15)));
		
		///////////////////////////////////////////////////////////////////////
		JPanel py = new JPanel();
		py.setAlignmentX(Component.LEFT_ALIGNMENT);
		py.setLayout(new BoxLayout(py, BoxLayout.X_AXIS));
		
		JPanel p = new JPanel();
		startbtn.add(Box.createRigidArea(new Dimension(63, 21)));
		startbtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.lightBlue));
		startbtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		startbtn.setEnabled(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		p.add(startbtn);
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		py.add(p);
		
		p = new JPanel();
		stopbtn.add(Box.createRigidArea(new Dimension(63, 21)));
		stopbtn.setUI(new BEButtonUI().setNormalColor(BEButtonUI.NormalColor.normal));
		stopbtn.setAlignmentX(Component.LEFT_ALIGNMENT);
		stopbtn.setEnabled(false);
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		p.add(stopbtn);
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		py.add(p);
		
		startbtn.addActionListener(this);
		stopbtn.addActionListener(this);
		
		buttonPanel.add(py);
		//////////////////////////////////////////////////////////////////////
		
		buttonPanel.add(Box.createRigidArea(new Dimension(1,15)));
		
		py = new JPanel();
		py.setAlignmentX(Component.LEFT_ALIGNMENT);
		py.setLayout(new BoxLayout(py, BoxLayout.X_AXIS));
		///////////////////
		s_clusternum.setOpaque(false);// add by jb2011：不填充默认背景色（否则放到白色面板板会很难看 ）
		s_clusternum.setPaintTicks(true);
		s_clusternum.setMajorTickSpacing(20);
		s_clusternum.setMinorTickSpacing(5);
		s_clusternum.setPaintLabels(true);
		s_clusternum.getAccessibleContext().setAccessibleName("SliderDemo.minorticks");
		s_clusternum.getAccessibleContext().setAccessibleDescription("SliderDemo.minorticksdescription");

		ChangeListener s_clusternum_listener = new SliderListener(l_clusternum);
		s_clusternum.addChangeListener(s_clusternum_listener);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		
		p.add(createVerticalHintBox(s_clusternum, l_clusternum));
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		py.add(p);
		///////////////////
		
		///////////////////
		s_iternum.setOpaque(false);// add by jb2011：不填充默认背景色（否则放到白色面板板会很难看 ）
		s_iternum.setPaintTicks(true);
		s_iternum.setMajorTickSpacing(20);
		s_iternum.setMinorTickSpacing(5);
		s_iternum.setPaintLabels(true);
		s_iternum.getAccessibleContext().setAccessibleName("SliderDemo.minorticks");
		s_iternum.getAccessibleContext().setAccessibleDescription("SliderDemo.minorticksdescription");
		
		ChangeListener s_iternum_listener = new SliderListener(l_iternum);
		s_iternum.addChangeListener(s_iternum_listener);
		
		p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.X_AXIS));
		p.add(Box.createRigidArea(new Dimension(5, 1)));
		
		p.add(createVerticalHintBox(s_iternum, l_iternum));
		p.add(Box.createRigidArea(new Dimension(5, 1)));// modified by jb2011, from HGAP10 to
		py.add(p);
		///////////////////
		buttonPanel.add(py);

		///////////////////////////////////////
		p = new JPanel();
		p.setAlignmentX(Component.LEFT_ALIGNMENT);	// add by jb2011
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		
		s_exponent.setOpaque(false);// add by jb2011：不填充默认背景色（否则放到白色面板板会很难看 ）
		s_exponent.putClientProperty("JSlider.isFilled", Boolean.TRUE);

		s_exponent.setPaintTicks(true);
		s_exponent.setMajorTickSpacing(5);
		s_exponent.setMinorTickSpacing(1);

		s_exponent.setPaintLabels(true);
		s_exponent.setSnapToTicks(true);

		s_exponent.getLabelTable().put(new Integer(11), new JLabel(new Integer(11).toString(), JLabel.CENTER));
		s_exponent.setLabelTable(s_exponent.getLabelTable());

		s_exponent.getAccessibleContext().setAccessibleName("SliderDemo.minorticks");
		s_exponent.getAccessibleContext().setAccessibleDescription("SliderDemo.minorticksdescription");
		
		ChangeListener s_exponent_listener = new SliderListener(l_exponent);
		s_exponent.addChangeListener(s_exponent_listener);
		
		createHorizonalHintBox(p, s_exponent, l_exponent);// getString("SliderDemo.ticks"));
		p.add(Box.createRigidArea(new Dimension(1, 5)));
		p.add(s_exponent);
		p.add(Box.createRigidArea(new Dimension(1, 5)));
		buttonPanel.add(p);
		buttonPanel.add(Box.createRigidArea(new Dimension(1, 10)));
		///////////////////////////////////////
		
		///////////////////////////////////////////////////////////
		JCheckBox showHorizontalLinesCheckBox = new JCheckBox("是否显示过程", flag_speed);
		showHorizontalLinesCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				flag_speed = ((JCheckBox) e.getSource()).isSelected();
				if(flag_speed) {
					s_speed.setEnabled(true);
				} else {
					s_speed.setEnabled(false);
				}
			}
		});
		buttonPanel.add(showHorizontalLinesCheckBox);
		///////////////////////////////////////////////////////////
		
		///////////////////////////////////////
		JPanel p_speed = new JPanel();
		p_speed.setAlignmentX(Component.LEFT_ALIGNMENT);	// add by jb2011
		p_speed.setLayout(new BoxLayout(p_speed, BoxLayout.Y_AXIS));
		// p.setBorder(new TitledBorder(getString("SliderDemo.ticks")));
		s_speed.setOpaque(false);// add by jb2011：不填充默认背景色（否则放到白色面板板会很难看 ）
		s_speed.putClientProperty("JSlider.isFilled", Boolean.TRUE);
		
		s_speed.setPaintTicks(true);
		s_speed.setMajorTickSpacing(5);
		s_speed.setMinorTickSpacing(1);
		
		s_speed.setPaintLabels(true);
		s_speed.setSnapToTicks(true);
		
		s_speed.getLabelTable().put(new Integer(11), new JLabel(new Integer(11).toString(), JLabel.CENTER));
		s_speed.setLabelTable(s_speed.getLabelTable());
		
		s_speed.getAccessibleContext().setAccessibleName("SliderDemo.minorticks");
		s_speed.getAccessibleContext().setAccessibleDescription("SliderDemo.minorticksdescription");
		s_speed.setEnabled(flag_speed);
		
		ChangeListener s_speed_listener = new SliderListener(l_speed);
		s_speed.addChangeListener(s_speed_listener);
		
		createHorizonalHintBox(p_speed, s_speed, l_speed);
		p_speed.add(Box.createRigidArea(new Dimension(1, 5)));
		p_speed.add(s_speed);
		p_speed.add(Box.createRigidArea(new Dimension(1, 5)));
		buttonPanel.add(p_speed);
		buttonPanel.add(Box.createRigidArea(new Dimension(1, 10)));
		///////////////////////////////////////
		
		buttonPanel.add(Box.createVerticalGlue());
		innerPanel.add(buttonPanel);
		innerPanel.add(Box.createRigidArea(new Dimension(30,1)));
		
		imagePanel.setLayout(null);
		innerPanel.add(imagePanel);
		
		innerPanel.add(Box.createRigidArea(new Dimension(20, 1)));
		
		add(demoPanel, BorderLayout.CENTER);
		
		// 初始化颜色; 与分类数有关
		int r=0,g=255,b=0;
		int c_step = 255/MAX_CLUSTERNUM;
		color_lists.add(Color.black);
		color_lists.add(Color.blue);
		color_lists.add(Color.cyan);
		color_lists.add(Color.darkGray);
		color_lists.add(Color.green);
		color_lists.add(Color.lightGray);
		color_lists.add(Color.orange);
		color_lists.add(Color.magenta);
		color_lists.add(Color.pink);
		color_lists.add(Color.red);
		color_lists.add(Color.yellow);
		for (int i = 0; i < MAX_CLUSTERNUM-11; i++) {
			Color c = new Color(r, g, b);
			color_lists.add(c);
			r += c_step;
			g -= c_step;
			b += c_step;
		}
    }
    
    public JFileChooser createFileChooser() {
    	// create a filechooser
    	JFileChooser fc = new JFileChooser();
    	// set the current directory to be the images directory
    	File swingFile = new File(OLD_FILEPATH);
    	if(swingFile.exists()) {
    	    fc.setCurrentDirectory(swingFile);
    	    fc.setSelectedFile(swingFile);
    	}
    	return fc;
    }
    
    public JFileChooser saveFileChooser() {
    	JFileChooser saveFc = new JFileChooser();
    	File swingFile = new File(OLD_FILEPATH);
    	if(swingFile.exists()) {
    		saveFc.setCurrentDirectory(swingFile);
    	    saveFc.setSelectedFile(swingFile);
    	}
		saveFc.addChoosableFileFilter(new FileFilter(){
    		public boolean accept(File f) {
    			if (f.isDirectory()) {
    				return true;
    			}
    			if (f.getName().endsWith(".csv")) {
    				return true;
    			}
    			return false;
    		}
    		public String getDescription() {
    			return "*.csv";
    		}
		});
		return saveFc;
    }
    
    public JButton createButton(Action a) {
    	JButton b = new JButton(a) {
			private static final long serialVersionUID = 1L;
			public Dimension getMaximumSize() {
	    		int width = Short.MAX_VALUE;
	    		int height = super.getMaximumSize().height;
	    		return new Dimension(width, height);
    	    }
    	};
    	return b;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == startbtn) {
        	startbtn.setEnabled(false);
        	stopbtn.setEnabled(true);
        	shutdownRequested = false;
        	
    		if(flag_speed) {
    			 processThread = new Thread(){
	    			public void run() {
	    				synchronized(this){
	    					// 中心轨迹重新初始化
	    					center_rooters = new CenterRooter[clusternum];
	    					for (int j = 0; j < clusternum; j++) {
	    						CenterRooter center_rooter = new CenterRooter();
	    						center_rooters[j] = center_rooter;
	    					}
	    					// -----------------------------------------------------------------------
	    					if(datas == null || datas.size() < 1 || exponent <= 1) {
	    			            return;
	    			        }
	    			        int num_data = datas.size();        // 数据行数
	    			        int num_d = datas.get(0).length;    // 数据维数
	    			        // 隶属度
	    			        double[][] U = new double[clusternum][num_data];
	    			        for (int i = 0; i < clusternum; i++) {
	    			            for (int j = 0; j < num_data; j++) {    // 随机赋值
	    			                U[i][j] = FCMAlgorithm.randGen.nextDouble()*10;
	    			            }
	    			        }
	    			        
	    			        for (int j = 0; j < num_data; j++) {        // 归一化
	    			            double sum_d = 0;
	    			            for (int i = 0; i < clusternum; i++) {      
	    			                sum_d += U[i][j];
	    			            }
	    			            for (int i = 0; i < clusternum; i++) {      
	    			                U[i][j] = U[i][j] / sum_d;
	    			            }
	    			        }
	    			        
	    			        /**循环--规定迭代次数作为结束条件*/
	    			        double[][] c = new double[clusternum][num_d];
	    			        double[] J = new double[iternum];
	    			        for (int iter_i = 0; !shutdownRequested && iter_i < iternum; iter_i++) {
	    			        	try{
	    			        		boolean brk = FCMAlgorithm.OneSteo(datas, center_rooters, clusternum, exponent, num_data, num_d, U, c, J, iter_i);
	    			        		for (int j = 0; j < num_data; j++) {
	    			        			int index = 0;
	    			        			double max = U[index][j];
	    			        			for (int i = 1; i < clusternum; i++) {
	    			        				if(max < U[i][j]) {
	    			        					index = i;
	    			        					max = U[index][j];
	    			        				}
	    			        			}
	    			        			datas_label.set(j, index+1);
	    			        		}
	    			        		repaintCoordinate(false);
	    			        		// 动画速度控制
	    			        		Thread.sleep(600/speed);
	    			        		if(brk) {
	    			        			break;
	    			        		}
	    			        	}catch(Exception e) {
	    			        		shutdownRequested = true;
	    			        	}
	    			        }
	    			        startbtn.setEnabled(true);
	    		        	stopbtn.setEnabled(false);
	    					// -----------------------------------------------------------------------
	    				}
	    			}
	    		};
	    		processThread.start();
    		} else {
    			processThread = new Thread(){
    				public void run() {
    					synchronized(this){
							// 中心轨迹重新初始化
							center_rooters = new CenterRooter[clusternum];
							for (int i = 0; i < clusternum; i++) {
								CenterRooter center_rooter = new CenterRooter();
								center_rooters[i] = center_rooter;
							}
							// 一次性调用
							FCMAlgorithm.fcm(datas, datas_label, center_rooters, clusternum, iternum, exponent);
							repaintCoordinate(false);
							startbtn.setEnabled(true);
							stopbtn.setEnabled(false);
						}
    				}
    			};
    			processThread.start();
    		}
        }
        if (e.getSource() == stopbtn){
        	if(processThread != null && processThread.isAlive()) {
        		shutdownRequested = true;
        	}
        	try {
				processThread.join();
				paintPointThread.join();
				startbtn.setEnabled(true);
				stopbtn.setEnabled(false);
			} catch (InterruptedException e1) {
			}
        }
    }
    
    /**滑动条监听*/
	class SliderListener implements ChangeListener {
		JLabel label;
		public SliderListener(JLabel label){
			this.label = label;
		}
		public void stateChanged(ChangeEvent e) {
			JSlider s1 = (JSlider) e.getSource();
			String name = label.getText();
			if(!"".equals(name)) {
				name = name.substring(0, name.indexOf(":"));
			}
			if("分类数".equals(name)) {
				clusternum = s1.getValue();
			} else if("迭代次数".equals(name)) {
				iternum = s1.getValue();
			} else if("指数".equals(name)) {
				exponent = s1.getValue();
			} else if("程序执行速度".equals(name)) {
				speed = s1.getValue();
			} else {
				JOptionPane.showMessageDialog(null, "参数有误", "提醒", JOptionPane.ERROR_MESSAGE);
			}
			label.setText(name+":"+s1.getValue());
		}
	}
	
	/** 重绘结果面板 */
	private void repaintCoordinate(final boolean firsttime) {
		paintPointThread = new Thread(){
			public void run() {
				SwingUtilities.invokeLater(new Runnable(){
			         public void run() {
			        	 if(datas != null) {
			        		 imagePanel.clear();
			        		 double loX=Double.MAX_VALUE, hiX=Double.MIN_VALUE, loY=Double.MAX_VALUE, hiY=Double.MIN_VALUE;
			        		 // 画分类结果
			        		 for (int i = 0; i < datas.size(); i++) {
			        			 double[] ds = datas.get(i);
			        			 if(firsttime) {
			        				 if(loX > ds[SHOW_COLUMN_X]) {
			        					 loX = ds[SHOW_COLUMN_X];
			        				 }
			        				 if(hiX < ds[SHOW_COLUMN_X]) {
			        					 hiX = ds[SHOW_COLUMN_X];
			        				 }
			        				 if(loY > ds[SHOW_COLUMN_Y]) {
			        					 loY = ds[SHOW_COLUMN_Y];
			        				 }
			        				 if(hiY < ds[SHOW_COLUMN_Y]) {
			        					 hiY = ds[SHOW_COLUMN_Y];
			        				 }
			        			 }
			        			 imagePanel.add(new CCPoint(ds[SHOW_COLUMN_X], ds[SHOW_COLUMN_Y], color_lists.get(datas_label.get(i))));
			        		 }
			        		 // 画中心点移动轨迹
			        		 if(flag_speed) {
			        			 for (int i = 0; i < center_rooters.length; i++) {
			        				 List<double[]> lis  = center_rooters[i].li;
			        				 for (int j = 0; j < lis.size(); j++) {
			        					 double[] ds = lis.get(j);
			        					 imagePanel.add(new CCPoint(ds[SHOW_COLUMN_X], ds[SHOW_COLUMN_Y], color_lists.get(i+1), new BasicStroke(3f)));
			        					 if(j > 0) {
			        						 Point2D.Double[] points = new Point2D.Double[2];
			        						 Point2D.Double double1 = new Point2D.Double();
			        						 double1.x = (lis.get(j))[SHOW_COLUMN_X];
			        						 double1.y = (lis.get(j))[SHOW_COLUMN_Y];
			        						 points[0] = double1;
			        						 Point2D.Double double2 = new Point2D.Double();
			        						 double2.x = (lis.get(j-1))[SHOW_COLUMN_X];
			        						 double2.y = (lis.get(j-1))[SHOW_COLUMN_Y];
			        						 points[1] = double2;
			        						 CCPolygon polygon = new CCPolygon(points, color_lists.get(i+1), color_lists.get(i+1), new BasicStroke(3f));
			        						 imagePanel.add(polygon);
			        					 }
			        				 }
			        			 }
			        		 }
			        		 // 首次加载保存离散点坐标适合
			        		 if(firsttime) {
			        			 double padding_X = (hiX-loX)/4.0;
			        			 double padding_Y = (hiY-loY)/4.0;
			        			 imagePanel.move(loX-padding_X, hiX+padding_X, loY-padding_Y, hiY+padding_Y);
			        		 }
			        		 imagePanel.repaint();
			        		 imagePanel.updateUI();
			        	 }
			         }
				});
			}
		};
		paintPointThread.start();
	}


	public static void createHorizonalHintBox(JPanel parent, JComponent c, JLabel l1) {
		parent.setAlignmentX(Component.LEFT_ALIGNMENT);
		parent.setBounds(15, 10, 100, 30);
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		
		l1.setAlignmentX(Component.LEFT_ALIGNMENT);
		parent.add(l1);
	}

	public static JPanel createVerticalHintBox(JComponent c, JLabel l1) {
		JPanel p = new JPanel();
		p.setOpaque(false);
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		l1.setAlignmentX(Component.CENTER_ALIGNMENT);
		p.add(l1);
		c.setAlignmentX(Component.CENTER_ALIGNMENT);
		p.add(c);

		p.setBorder(BorderFactory.createEmptyBorder(10, 0, 5, 0));
		return p;
	}
	
	public JMenuBar createMenus() {
		JMenuBar menuBar = new JMenuBar();
		menuBar.getAccessibleContext().setAccessibleName("");

		JMenu fileMenu = (JMenu) menuBar.add(new JMenu("文件"));
		createMenuItem(fileMenu, "打开文件", "", "", new OpenFileAction());
		fileMenu.addSeparator();
		createMenuItem(fileMenu, "保存", "", "", new SaveFileAction());
		createMenuItem(fileMenu, "另存为...", "", "", new SaveAsFileAction());
		fileMenu.addSeparator();
		createMenuItem(fileMenu, "保存界面为图片...", "", "", new SaveAsPictureAction());
		fileMenu.addSeparator();
		createMenuItem(fileMenu, "打开文件位置", "", "", new FilesLocationAction());
		fileMenu.addSeparator();
		createMenuItem(fileMenu, "退出系统", "", "", new ExitSys());
		
		JMenu preferenceMenu = (JMenu) menuBar.add(new JMenu("选项"));
		JMenuItem mi = createCheckBoxMenuItem(preferenceMenu, "显示网格", "", "", new CoordinateAction());
		mi.setSelected(true);

		return menuBar;
	}
	
	public JMenuItem createMenuItem(JMenu menu, String label, String mnemonic, String accessibleDescription, Action action) {
		JMenuItem mi = (JMenuItem) menu.add(new JMenuItem(label));
		mi.addActionListener(action);
		if(action == null) {
			mi.setEnabled(false);
		}
		return mi;
	}
	
	private JMenuItem createCheckBoxMenuItem(JMenu menu, String label, String mnemonic, String accessibleDescription, Action action) {
		JCheckBoxMenuItem mi = (JCheckBoxMenuItem)menu.add(new JCheckBoxMenuItem(label));
		mi.addActionListener(action);
		if(action == null) {
			mi.setEnabled(false);
		}
		return mi;
	}
	
	class OpenFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = createFileChooser();
			int result = fc.showOpenDialog(innerPanel);			// show the filechooser
			if(result == JFileChooser.APPROVE_OPTION) {			// if we selected an image, load the image
				try {
					boolean cancel = false;
					// OLD_FILEPATH = fc.getSelectedFile().getPath();
					List<List<String>> file_lists = new ArrayList<List<String>>();
					int max_num = Integer.MAX_VALUE;		// 最大的行个数
					BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(fc.getSelectedFile().getPath()), ENCODE));
					CSVFileUtil csvFileUtil = new CSVFileUtil(br);
					String inString = "";
					while ((inString = csvFileUtil.readLine()) != null) {
						List<String> ling_list = csvFileUtil.fromCSVLinetoArray(inString);
						file_lists.add(ling_list);
						if(max_num > ling_list.size()) {
							max_num = ling_list.size();
						} else if(max_num < ling_list.size()) {
							int rs = JOptionPane.showConfirmDialog(innerPanel, "传入数据列数值不一致，是否继续？","提示", JOptionPane.YES_OPTION);
							if (rs == JOptionPane.NO_OPTION) {
								cancel = true; break;
							}
						}
						if(max_num < 2) {
							JOptionPane.showMessageDialog(null, "文件列数至少为两列！", "提醒", JOptionPane.ERROR_MESSAGE);
							cancel = true; break;
						}
					}
					br.close();
					
					// 预处理
					if(!cancel) {
						datas = new ArrayList<double[]>();
						datas_label = new ArrayList<Integer>();				// 初始化分类均为0类;
						if(file_lists != null && file_lists.size() > 0) {
							for (List<String> list : file_lists) {
								double[] data = new double[max_num];
								for (int i = 0; i < max_num; i++) {
									data[i] = CommUtils.null2Double(list.get(i));
								}
								datas.add(data);
								datas_label.add(0);
							}
							startbtn.setEnabled(true);
							stopbtn.setEnabled(false);
							
							// 初始化中心的轨迹
							center_rooters = new CenterRooter[clusternum];
				    		for (int i = 0; i < clusternum; i++) {
				    			CenterRooter center_rooter = new CenterRooter();
				    			center_rooters[i] = center_rooter;
				    		}
							repaintCoordinate(true);
						}
					}
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(null, "文件读取失败！", "提醒", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
	}
	
	class SaveFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(datas == null || datas.size() < 1 || datas_label == null || datas_label.size() < 1) {
				JOptionPane.showMessageDialog(null, "还没有运行结果！", "提醒", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(stopbtn.isEnabled()) {
				JOptionPane.showMessageDialog(null, "正在计算结果，请稍等~", "提醒", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
			String time_str = df.format(new Date());
			
			String name = "fcn_result"+time_str+".csv";
			File file = new File(OLD_FILEPATH);
			String path = file.getPath();
			if(path.indexOf(".") > 0) {
				path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator + name;
			} else {
				path +=  File.separator + name;
			}
			file = new File(path);
			
			boolean save = true;
			if(file .exists()){
				int ok = JOptionPane.showConfirmDialog(innerPanel, file + "文件已经存在,是否覆盖!", "文件存在", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
				if (ok == JOptionPane.NO_OPTION) {
					save = false;
				}
			}
			if(save) {		// 保存
				saveResult(file, datas, datas_label);
			}
		}
	}
	
	class SaveAsFileAction extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			if(datas == null || datas.size() < 1 || datas_label == null || datas_label.size() < 1) {
				JOptionPane.showMessageDialog(null, "还没有运行结果！", "提醒", JOptionPane.ERROR_MESSAGE);
				return;
			}
			if(stopbtn.isEnabled()) {
				JOptionPane.showMessageDialog(null, "正在计算结果，请稍等~", "提醒", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			JFileChooser fc = saveFileChooser();
			int result = fc.showSaveDialog(innerPanel);
			if(result == JFileChooser.APPROVE_OPTION) {
				File file = fc.getSelectedFile();
				boolean save = true;
				if(file .exists()){
					int ok = JOptionPane.showConfirmDialog(fc, file + "文件已经存在,是否覆盖!", "文件存在", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (ok == JOptionPane.NO_OPTION) {
						save = false;
					}
				}
				if(save) {		// 保存
					String path = file.getPath();
					if(path.indexOf(".") > 0) {
						path = path.substring(0, path.indexOf(".")) + ".csv";
					} else {
						path = path + ".csv";
					}
					file = new File(path);
					saveResult(file, datas, datas_label);
				}
			}
		}
	}
	
	/**
	 * 保存运行结果
	 */
	private void saveResult(File file, List<double[]> datas, List<Integer> datas_label) {
		if(file == null || datas == null || datas.size() < 1 || datas_label == null || datas_label.size() < 1) {
			return;
		}
		BufferedWriter csvFileOutputStream = null;
		try {
			csvFileOutputStream = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "UTF-8"), 1024);
			for (int i = 0; i < datas.size(); i++) {
				double[] ds = datas.get(i);
				int length = ds.length + 1;
				String[] strArray = new String[length];
				for (int j = 0; j < ds.length; j++) {
					strArray[j] = ds[j]+"";
				}
				strArray[length-1] = datas_label.get(i)+"";
				String str = CSVFileUtil.toCSVLine(strArray);
				csvFileOutputStream.write(str);
				csvFileOutputStream.newLine();
			}
			csvFileOutputStream.flush();
		} catch (Exception e) {
		} finally {
			if(csvFileOutputStream != null) {
				try {
					csvFileOutputStream.close();
				} catch (IOException e) {
				}
			}
		}
	}
	
	class SaveAsPictureAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			try {
				File file = new File(OLD_FILEPATH);
				String path = file.getAbsolutePath();
				
				SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HHmmss");
				String time_str = df.format(new Date());
				String name = "fcn_result"+time_str+".eps";
				
				if(path.indexOf(".") > 0) {
					path = path.substring(0, path.lastIndexOf("."));
					path = path.substring(0, path.lastIndexOf(File.separator)) + File.separator;
				} else {
					path += File.separator;
				}
				path += "files";
				
				System.setProperty("user.home", path);
				ExportDialog export = new ExportDialog();
				export.showExportDialog(innerPanel, "导出图片为 ...", imagePanel, name);
			} catch (Exception ee) {
				JOptionPane.showMessageDialog(null, "保存出错了", "提醒", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class CoordinateAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			boolean status = ((JCheckBoxMenuItem)e.getSource()).isSelected();
			if(status) {
				imagePanel.setGridVisible(true);
			} else {
				imagePanel.setGridVisible(false);
			}
			imagePanel.repaint();
		}
	}
	
	class FilesLocationAction extends AbstractAction {
		private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			String[] cmd = new String[5];  
            cmd[0] = "cmd";  
            cmd[1] = "/c";  
            cmd[2] = "start";  
            cmd[3] = " ";  
            cmd[4] = SOURCE_FILEPATH;  
            try {
				Runtime.getRuntime().exec(cmd);
			} catch (IOException e1) {
				JOptionPane.showMessageDialog(null, "仿佛打不开了，楼主对不起您。", "提醒", JOptionPane.ERROR_MESSAGE);
			}
		}
	}
	
	class ExitSys extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			int rs = JOptionPane.showConfirmDialog(innerPanel, "确实要退出系统？","提示",JOptionPane.YES_OPTION);
			if (rs == JOptionPane.YES_OPTION) {
				System.exit(0);
			}
		}
	}
	
}
