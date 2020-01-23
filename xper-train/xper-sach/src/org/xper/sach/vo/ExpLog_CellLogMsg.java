package org.xper.sach.vo;


import com.thoughtworks.xstream.XStream;

public class ExpLog_CellLogMsg {
	
	
	String cellId;
	String notes;	
	long depth;

	String animalID;
	String animalHemisphere;
	String animalTargetArea;
	
	double[] coordsBV;
	double[] coordsST;
	double[] coordsAS;
	
	
	public ExpLog_CellLogMsg(String id,String notes,long depth) 
	{
		super();
		setCellId(id);
		setNotes(notes);
		setDepth(depth);
	}
	
	public ExpLog_CellLogMsg(
			String id,String notes,long depth,
			String animalId,String animalHemisphere,String animalTargetArea,
			double[] targetCoords_BV,double[] targetCoords_ST,double[] targetCoords_AS) 
	{
		super();

		setCellId(id);
		setNotes(notes);
		setDepth(depth);
		setAnimalID(animalId);
		setAnimalHemisphere(animalHemisphere);
		setAnimalTargetArea(animalTargetArea);
		setCoordsBV(targetCoords_BV);
		setCoordsST(targetCoords_ST);
		setCoordsAS(targetCoords_AS);
		
	}


	static XStream xstream = new XStream();

	static {
		xstream.alias("ExpLog_CellLogMsg", ExpLog_CellLogMsg.class);
	}
	
	public static ExpLog_CellLogMsg fromXml(String xml) {
		return (ExpLog_CellLogMsg)xstream.fromXML(xml);
	}
	
	public static String toXml(ExpLog_CellLogMsg msg) {
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

	public String getAnimalID() {
		return animalID;
	}

	public void setAnimalID(String animalID) {
		this.animalID = animalID;
	}

	public String getAnimalHemisphere() {
		return animalHemisphere;
	}

	public void setAnimalHemisphere(String animalHemisphere) {
		this.animalHemisphere = animalHemisphere;
	}

	public String getAnimalTargetArea() {
		return animalTargetArea;
	}

	public void setAnimalTargetArea(String animalTargetArea) {
		this.animalTargetArea = animalTargetArea;
	}

	public double[] getCoordsBV() {
		return coordsBV;
	}

	public void setCoordsBV(double[] coordsBV) {
		this.coordsBV = coordsBV;
	}

	public double[] getCoordsST() {
		return coordsST;
	}

	public void setCoordsST(double[] coordsST) {
		this.coordsST = coordsST;
	}

	public double[] getCoordsAS() {
		return coordsAS;
	}

	public void setCoordsAS(double[] coordsAS) {
		this.coordsAS = coordsAS;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String id) {
		this.cellId = id;
	}
	
}
