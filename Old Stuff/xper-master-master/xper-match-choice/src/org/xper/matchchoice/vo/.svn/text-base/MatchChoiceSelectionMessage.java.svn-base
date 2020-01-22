package org.xper.matchchoice.vo;


import com.thoughtworks.xstream.XStream;

public class MatchChoiceSelectionMessage {
	int selection;

	public MatchChoiceSelectionMessage(int selection) {
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
		xstream.alias("ChoiceSelectionMessage", MatchChoiceSelectionMessage.class);
	}
	
	public static MatchChoiceSelectionMessage fromXml (String xml) {
		return (MatchChoiceSelectionMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (MatchChoiceSelectionMessage msg) {
		return xstream.toXML(msg);
	}
	
}
