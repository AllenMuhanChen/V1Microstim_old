package org.xper.choice.vo;


import com.thoughtworks.xstream.XStream;

public class ChoiceSelectionMessage {
	int selection;

	public ChoiceSelectionMessage(int selection) {
		super();
		this.selection = selection;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}
	
	static XStream xstream = new XStream();

	static {
		xstream.alias("ChoiceSelectionMessage", ChoiceSelectionMessage.class);
	}
	
	public static ChoiceSelectionMessage fromXml (String xml) {
		return (ChoiceSelectionMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (ChoiceSelectionMessage msg) {
		return xstream.toXML(msg);
	}
	
}
