package org.xper.sach.testing;


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import org.xper.sach.analysis.GAGenAnalysisPlot;
import org.xper.sach.util.SachDbUtil;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

public class DisplayImage {

    public static void main(String avg[]) throws IOException
    {
        DisplayImage abc=new DisplayImage();
    }

    public DisplayImage() throws IOException
    {
        
        JFrame frame=new JFrame();
        frame.setLayout(new FlowLayout());
        frame.setSize(300,300);
        
//        JList list = new JList(); //data has type Object[]
//    	list.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
//    	list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
//    	list.setVisibleRowCount(-1);
//    	list.setListData(images);

    	
//    	JScrollPane listScroller = new JScrollPane(list);
//    	listScroller.setPreferredSize(new Dimension(800,500));
        
        SachDbUtil dbUtil = GAGenAnalysisPlot.setDbUtil();
        byte[] b = dbUtil.readThumbnail(1410888966869917L);
        BufferedImage bimg = ImageIO.read(new ByteArrayInputStream(b));
        
        // change color:
        double thisFR = 8.5;
        double maxFR = 45.8;
        int redness = (int) (255*thisFR/maxFR);
        Color oldColor = new Color(bimg.getRGB(0, 0));
//		Color newColor = new Color(redness,oldColor.getGreen(),oldColor.getBlue());
		Color newColor = new Color(redness,0,0);
        changeColor(bimg,oldColor,newColor);
        
        
        
//        // add text:
//		bimg.createGraphics().drawImage(image, 0, 0, this);
//
//		// add text
//        Graphics graphics = bimg.getGraphics();
////        graphics.setColor(Color.CYAN);
////        graphics.fillRect(0, 0, 50, 50);
//        graphics.setFont(f);
//        graphics.setColor(Color.BLACK);		
//		graphics.drawString(s,1,100);
		
		
        
        ImageIcon icon = new ImageIcon(bimg);

//    	String currDir = System.getProperty("user.dir")+"/images/";
//    	BufferedImage bimg = ImageIO.read(new File(currDir+"stimfull_0.jpg"));
//    	ImageIcon icon = new ImageIcon(bimg);

//        ImageIcon icon = new ImageIcon(currDir+"stimfull_0.jpg");
        
        // resize:
        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);
        frame.add(new JLabel(icon,JLabel.CENTER),BorderLayout.CENTER);

        

//        JLabel lbl=new JLabel();
//        lbl.setIcon(icon);
//        lbl.setPreferredSize(new Dimension(200,200));
////        lbl.setBackground(new Color(1f,0f,0f));
////        lbl.setForeground(new Color(1f,0f,0f));
//        lbl.setBorder(BorderFactory.createLineBorder(new Color(1f,0f,0f),8));
//        frame.add(lbl,BorderLayout.CENTER);

//		JLabel l = new JLabel();
//		l.setIcon(icon);
        //l.setPreferredSize(new Dimension(100,100));
        //double normFR = dataset.getValue(0,n).doubleValue() / dataset.getValue(0,0).doubleValue();
        //l.setBorder(BorderFactory.createLineBorder(new Color((float)normFR,0f,0f),8));
//        lbls.add(l);
        
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
    
    
    public void changeColor(BufferedImage img, Color oldColor, Color newColor) {
        final int oldRGB = oldColor.getRGB();
        final int newRGB = newColor.getRGB();
        for (int x = 0; x < img.getWidth(); x++) {
            for (int y = 0; y < img.getHeight(); y++) {
                if (img.getRGB(x, y) == oldRGB)
                    img.setRGB(x, y, newRGB);
            }
        }
    }
}