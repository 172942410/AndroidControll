package com.capture.window;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class ScreenPanel extends JPanel{
	
	public static int width = 480;
	public static int height = 800;

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		String imgPath = "/tmp/screen.jpg";
		BufferedImage image;
		try {
			image = ImageIO.read(new FileInputStream(imgPath));
			g.drawImage(image, 0, 0, width, height, this);
			image = null;
			System.gc();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void run() {
		// TODO Auto-generated method stub
		try {
			System.out.println(getFileSizes(new File("/tmp/screen.jpg"))+"--------------");
			if(getFileSizes(new File("/tmp/screen.jpg")) != 0){
				repaint();
			}
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		try {
			Thread.sleep(500);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public long getFileSizes(File f) throws Exception{//取得文件大小
	       long s=0;
	       if (f.exists()) {
	           FileInputStream fis = null;
	           fis = new FileInputStream(f);
	          s= fis.available();
	       } else {
	           f.createNewFile();
	           System.out.println("文件不存在");
	       }
	       return s;
	    }
}
