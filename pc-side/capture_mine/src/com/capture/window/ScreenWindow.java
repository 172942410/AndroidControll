package com.capture.window;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JPanel;

import com.android.ddmlib.IDevice;
import com.capture.util.FileHelper;
import com.capture.util.MyUtil;

import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.awt.event.ActionEvent;

/**
 * @author harveyprince 用于展示爪机端画面并进行控制
 *
 */
public class ScreenWindow extends JFrame {

	public String adbLocation = "";
	public OperateAndroid oa = null;
	public boolean lock = true;
	static Socket socket = null;
	static BufferedOutputStream myout;
	static BufferedInputStream myin;
	public static IDevice device = null;

	/**
	 * Create the frame.
	 */
	public ScreenWindow(IDevice device) {
		
		this.device = device;
		
		oa = OperateAndroid.getOperateAndroid(device);
		
		System.out.println("screen width:"+oa.getScreenWidth()+" screen height:"+oa.getScreenHeight());

		double width = oa.getScreenWidth()*800/oa.getScreenHeight();
		double height = 800.0;
		
		adbLocation = "/Users/harveyprince/Library/Android/sdk/platform-tools";
		if (adbLocation != null && adbLocation.length() != 0) {
			adbLocation += File.separator + "adb";
		} else {
			adbLocation = "adb";
		}
		
		// adb 指令
		try {
			Runtime.getRuntime().exec(adbLocation+" shell am broadcast -a NotifyServiceStop");
			Thread.sleep(3000);
			Runtime.getRuntime().exec(adbLocation+" forward tcp:12580 tcp:10086"); // 端口转换
			Thread.sleep(3000);
			Runtime.getRuntime().exec(adbLocation+" shell am broadcast -a NotifyServiceStart");
			Thread.sleep(3000);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 560, 840);
		getContentPane().setLayout(null);

		ScreenPanel panel = new ScreenPanel();
		panel.setBounds(0, 0,(int) width,(int) height);
		panel.setBackground(Color.BLACK);
		panel.height = (int)height;
		panel.width = (int)width;
		getContentPane().add(panel);
		
		JPanel panel1 = new JPanel();
		panel1.setBounds(480, 0, 80, 800);
		panel1.setBackground(Color.DARK_GRAY);
		getContentPane().add(panel1);

		JButton btnNewButton = new JButton("home");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oa.press(OperateAndroid.HOME);
			}
		});
		panel1.add(btnNewButton);

		JButton btnNewButton_1 = new JButton("back");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oa.press(OperateAndroid.BACK);
			}
		});
		panel1.add(btnNewButton_1);

		JButton btnNewButton_2 = new JButton("menu");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				oa.press(OperateAndroid.MENU);
			}
		});
		panel1.add(btnNewButton_2);
		
		panel.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					oa.touchUp((int) ((e.getX())*(oa.getScreenWidth()/width)), (int) ((e.getY())*(oa.getScreenHeight()/height)));
					System.out.println("x:" + (int) (e.getX()) + " y:" + (int) (e.getY()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				System.out.println("mouseReleased");
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					oa.touchDown((int) ((e.getX())*(oa.getScreenWidth()/width)), (int) ((e.getY())*(oa.getScreenHeight()/height)));
					System.out.println("x:" + (int) (e.getX()) + " y:" + (int) (e.getY()));
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				System.out.println("mousePressed");
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
		});
		
		panel.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				// TODO Auto-generated method stub
				try {
					oa.touchMove((int) ((e.getX())*(oa.getScreenWidth()/width)), (int) ((e.getY())*(oa.getScreenHeight()/height)));
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		Thread th = new Thread(){
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try
				{
					InetAddress serveraddr = null;
					serveraddr = InetAddress.getByName("127.0.0.1");
					System.out.println("TCP 1111" + "C: Connecting...");
					socket = new Socket(serveraddr, 12580);
					String str = "getfile";
					System.out.println("TCP 221122" + "C:RECEIVE");
					myout = new BufferedOutputStream(socket.getOutputStream());
					myin = new BufferedInputStream(socket.getInputStream());
					BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
					myout.write(str.getBytes());
					myout.flush();
					boolean flag = true;
					while (flag)
					{
						String strFormsocket = readFromSocket(myin);
						System.out.println(strFormsocket);
						System.out.println(System.currentTimeMillis());
						if(strFormsocket.equals("file")){
							/* 接收文件数据，4字节文件长度，4字节文件格式，其后是文件数据 */
							/* 准备接收文件数据 */
							/*try
							{
								myout.write("prepare ready OK".getBytes());
								myout.flush();
							} catch (IOException e)
							{
								e.printStackTrace();
							}*/
							while(true){
								byte[] filelength = new byte[4];
								byte[] fileformat = new byte[4];
								byte[] filebytes = null;
								
								/* 从socket流中读取完整文件数据 */
								filebytes = receiveFileFromSocket(myin, myout, filelength, fileformat);
								
								// Log.v(Service139.TAG, "receive data =" + new
								// String(filebytes));
								try
								{
									/* 生成文件 */
									File file = FileHelper.newFile("screen.jpg");
									FileHelper.writeFile(file, filebytes, 0, filebytes.length);
									panel.run();
								} catch (IOException e)
								{
									e.printStackTrace();
								}
								
							}
							/*try
							{
								myout.write("getfile".getBytes());
								myout.flush();
							} catch (IOException e)
							{
								e.printStackTrace();
							}*/
						}
					}

				} catch (UnknownHostException e1)
				{
					System.out.println("TCP 331133" + "ERROR:" + e1.toString());
				} catch (Exception e2)
				{
					System.out.println("TCP 441144" + "ERROR:" + e2.toString());
				} finally
				{
					try
					{
						if (socket != null)
						{
							socket.close();
							System.out.println("socket.close()");
						}
					} catch (IOException e)
					{
						System.out.println("TCP 5555" + "ERROR:" + e.toString());
					}
				}
			}
		};
		
		th.start();
		
	}
	
	/* 从InputStream流中读数据 */
	public static String readFromSocket(InputStream in)
	{
		int MAX_BUFFER_BYTES = 4000;
		String msg = "";
		byte[] tempbuffer = new byte[MAX_BUFFER_BYTES];
		try
		{
			int numReadedBytes = in.read(tempbuffer, 0, tempbuffer.length);
			msg = new String(tempbuffer, 0, numReadedBytes, "utf-8");

			tempbuffer = null;
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return msg;
	}
	
	/**
	 * 功能：从socket流中读取完整文件数据
	 * 
	 * InputStream in：socket输入流
	 * 
	 * byte[] filelength: 流的前4个字节存储要转送的文件的字节数
	 * 
	 * byte[] fileformat：流的前5-8字节存储要转送的文件的格式（如.apk）
	 * 
	 * */
	public static byte[] receiveFileFromSocket(InputStream in, OutputStream out, byte[] filelength, byte[] fileformat)
	{
		byte[] filebytes = null;// 文件数据
		try
		{
			in.read(filelength);// 读文件长度
			int filelen = MyUtil.bytesToInt(filelength);// 文件长度从4字节byte[]转成Int
			String strtmp = "read file length ok:" + filelen;
			out.write(strtmp.getBytes("utf-8"));
			out.flush();

			filebytes = new byte[filelen];
			int pos = 0;
			int rcvLen = 0;
			while ((rcvLen = in.read(filebytes, pos, filelen - pos)) > 0)
			{
				pos += rcvLen;
			}
			out.write("read file ok".getBytes("utf-8"));
			out.flush();
		} catch (Exception e)
		{
			e.printStackTrace();
		}
		return filebytes;
	}
	
}
