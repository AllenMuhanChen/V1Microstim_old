package org.xper.sach.util;

import org.dom4j.Document;
import org.dom4j.Node;
import org.xper.drawing.Coordinates2D;

public class SachXmlUtil {
	public static Coordinates2D getTargetPosition(Document doc) {
		Node n = doc.selectSingleNode("/StimSpec/targetPosition");
		return getCooridates2D(n);
	}
	
	static Coordinates2D getCooridates2D(Node n) {
		String x = n.selectSingleNode("x").getText();
		String y = n.selectSingleNode("y").getText();
		return new Coordinates2D(Double.parseDouble(x), Double.parseDouble(y));
	}
	
	public static double getTargetEyeWinSize (Document doc) {
		Node n = doc.selectSingleNode("/StimSpec/targetEyeWinSize");
		String s = n.getText();
		return Double.parseDouble(s);
	}
	
	public static long getTargetIndex (Document doc) {
		Node n = doc.selectSingleNode("/StimSpec/targetIndex");
		String s = n.getText();
		return Long.parseLong(s);
	}
	
	public static long getReward(Document doc) {
		Node n = doc.selectSingleNode("/StimSpec/reward");
		String s = n.getText();
		return Long.parseLong(s);
	}
	
	// JK modify the reward for dynamic reward
	public static void  setReward(Document doc,  long reward) {
		Node n = doc.selectSingleNode("/StimSpec/reward");	
		Long li = reward;		
		n.setText(li.toString());
				
		return;
	}
}
