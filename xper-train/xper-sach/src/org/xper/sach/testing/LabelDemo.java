package org.xper.sach.testing;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.*;

public class LabelDemo
{
   //-----------------------------------------------------------------
   //  Creates and displays the primary application frame.
   //-----------------------------------------------------------------
   public static void main (String[] args)
   {
      JFrame frame = new JFrame ("Label Demo");
      frame.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE);

      String currDir = System.getProperty("user.dir")+"/images/";

      ImageIcon icon = new ImageIcon (currDir+"stimfull_0.jpg");

      JLabel label1, label2, label3;

      label1 = new JLabel ("Devil Left", icon, SwingConstants.CENTER);

      label2 = new JLabel ("Devil Right", icon, SwingConstants.CENTER);
      label2.setHorizontalTextPosition (SwingConstants.LEFT);
      label2.setVerticalTextPosition (SwingConstants.BOTTOM);

      label3 = new JLabel ("Devil Above", icon, SwingConstants.CENTER);
      label3.setHorizontalTextPosition (SwingConstants.CENTER);
      label3.setVerticalTextPosition (SwingConstants.BOTTOM);

      JPanel panel = new JPanel();
      panel.setBackground (Color.cyan);
      panel.setPreferredSize (new Dimension (600, 600));
      panel.add (label1);
      panel.add (label2);
      panel.add (label3);

      frame.getContentPane().add(panel);
      frame.pack();
      frame.setVisible(true);
   }
}