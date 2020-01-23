package org.xper.sach.testing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class TestTextOnImage extends JFrame {

	String s;
	ImageIcon img;
	Image image;
	JPanel p;
	JLabel label;
	JLabel text;
	public TestTextOnImage()
	{
		s = "Hi hey howdy";
		Font f = new Font("Serif",Font.BOLD,12);
		text = new JLabel("Hi hello");
		text.setFont(f);

		MediaTracker mt = new MediaTracker(this);
		
		String currDir = System.getProperty("user.dir")+"/images/";
		image = Toolkit.getDefaultToolkit().createImage(currDir+"stimfull_0.jpg");
		mt.addImage(image,0);
		try{mt.waitForID(0);}catch(InterruptedException ie){}
		int width = image.getWidth(null);
		int height = image.getHeight(null);
		BufferedImage bimg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		
		// change color
		
		
		bimg.createGraphics().drawImage(image, 0, 0, this);

		// add text
        Graphics graphics = bimg.getGraphics();
//        graphics.setColor(Color.CYAN);
//        graphics.fillRect(0, 0, 50, 50);
        graphics.setFont(f);
        graphics.setColor(Color.BLACK);		
		graphics.drawString(s,1,100);
		
		
		img = new ImageIcon(bimg);
		label = new JLabel(img);
		p = new JPanel();
		p.add(label);
		getContentPane().add(p);
	}
	
	public static void main(String args[])
	{
		TestTextOnImage tt = new TestTextOnImage();
		tt.setDefaultCloseOperation(EXIT_ON_CLOSE);
		tt.setSize(750,600);
		tt.setVisible(true);
	}
	
	
}
