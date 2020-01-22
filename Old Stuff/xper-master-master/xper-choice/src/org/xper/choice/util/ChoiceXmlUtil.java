package org.xper.choice.util;

import org.dom4j.Document;

public class ChoiceXmlUtil {
	public static long getChoiceReward(Document doc, String xpath) {
		String reward = doc.selectSingleNode(xpath).getText();
		return Long.parseLong(reward);
	}
}
