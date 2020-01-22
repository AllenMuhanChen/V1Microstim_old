package org.xper.match.util;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.Node;
import org.xper.drawing.RGBColor;

public class MatchXmlUtil {
	public static List<RGBColor> getTargetColor(Document doc) {
		List<RGBColor> targetColors = new ArrayList<RGBColor>();
		List<?> nodeList = doc.selectNodes("/StimSpec/targetColor");
		for (int i = 0; i < nodeList.size(); i ++) {
			Node n = (Node)nodeList.get(i);
			targetColors.add(getTargetColor(n));
		}
		return targetColors;
	}
	
	static RGBColor getTargetColor(Node colorNode) {
		String r = colorNode.selectSingleNode("red").getText();
		String g = colorNode.selectSingleNode("green").getText();
		String b = colorNode.selectSingleNode("blue").getText();
		return new RGBColor(Float.parseFloat(r), Float.parseFloat(g), Float.parseFloat(b));
	}
}
