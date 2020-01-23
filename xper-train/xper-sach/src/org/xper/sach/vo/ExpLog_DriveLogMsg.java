package org.xper.sach.vo;


import com.thoughtworks.xstream.XStream;

public class ExpLog_DriveLogMsg {
	
	long depth;
	String notes;				
	
	public ExpLog_DriveLogMsg(long depth,String notes) 
	{
		super();
		setDepth(depth);
		setNotes(notes);
	}

	static XStream xstream = new XStream();

	static {
		xstream.alias("ExpLog_DriveLogMsg", ExpLog_DriveLogMsg.class);
	}
	
	public static ExpLog_DriveLogMsg fromXml(String xml) {
		return (ExpLog_DriveLogMsg)xstream.fromXML(xml);
	}
	
	public static String toXml(ExpLog_DriveLogMsg msg) {
		return xstream.toXML(msg);
	}

	public long getDepth() {
		return depth;
	}

	public void setDepth(long depth) {
		this.depth = depth;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	
}
