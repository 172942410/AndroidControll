package com.capture.window;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import com.android.ddmlib.AndroidDebugBridge;
import com.android.ddmlib.IDevice;

import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.io.File;
import java.awt.event.ActionEvent;

/**
 * @author harveyprince
 * 这个界面用于展示所有的设备列表
 *
 */
public class MainWindow extends JFrame {

	private JPanel contentPane;
	
	
	public static String targetNum = null;
	public static IDevice device = null;
	public String adbLocation = "";
	

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public void initVal(){
		adbLocation = "/Users/harveyprince/Library/Android/sdk/platform-tools";
		if (adbLocation != null && adbLocation.length() != 0) {
			adbLocation += File.separator + "adb";
		} else {
			adbLocation = "adb";
		}
		
	}
	
	JList list;
	IDevice[] devices;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		initVal();
		
		AndroidDebugBridge.init(false);
		AndroidDebugBridge bridge = AndroidDebugBridge.createBridge(
				adbLocation, true);
		
		JLabel label = new JLabel("设备列表");
		contentPane.add(label, BorderLayout.NORTH);
		
		list = new JList();
		contentPane.add(list, BorderLayout.CENTER);
		list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		
		JButton btnNewButton = new JButton("刷新");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//刷新获取设备列表
				int count = 0;
				while (bridge.hasInitialDeviceList() == false) {
					try {
						Thread.sleep(100);
						count++;
					} catch (InterruptedException error) {
					}

					if (count > 100) {
						System.err.println("Timeout getting device list!");
						return;
					}
				}
				devices = bridge.getDevices();
				DefaultListModel listModel = new DefaultListModel();
				for (IDevice d : devices) {
					listModel.addElement(d.getSerialNumber());
				}
				list.setModel(listModel);
				
			}
		});
		contentPane.add(btnNewButton, BorderLayout.SOUTH);
		
		list.addListSelectionListener(new ListSelectionListener() {
			
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// TODO Auto-generated method stub
				if (e.getValueIsAdjusting() == false) {
					IDevice dev = devices[e.getFirstIndex()];
					ScreenWindow frame = new ScreenWindow(dev);
					frame.setVisible(true);
				}
			}
		});
		
	}
	
	

}
