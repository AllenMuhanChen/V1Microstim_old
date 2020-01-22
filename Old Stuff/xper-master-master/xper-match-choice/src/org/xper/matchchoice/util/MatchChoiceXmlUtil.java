package org.xper.matchchoice.util;

import org.dom4j.Document;
import org.dom4j.Node;
import org.xper.drawing.RGBColor;

public class MatchChoiceXmlUtil {
	public static int getChoiceReward(Document doc, String xpath) {
		String reward = doc.selectSingleNode(xpath).getText();
		return Integer.parseInt(reward);
	}
	public static double getChoiceSize(Document doc, String xpath) {
		String size = doc.selectSingleNode(xpath).getText();
		return Double.parseDouble(size);
	}
	public static double getChoiceDistance(Document doc, String xpath) {
		String size = doc.selectSingleNode(xpath).getText();
		return Double.parseDouble(size);
	}
	public static RGBColor getChoiceColor(Document doc, String xpath) {
		Node root = doc.selectSingleNode(xpath);
		String r = root.selectSingleNode("red").getText();
		String g = root.selectSingleNode("green").getText();
		String b = root.selectSingleNode("blue").getText();
		return new RGBColor(Float.parseFloat(r), Float.parseFloat(g), Float.parseFloat(b));
	}
}
