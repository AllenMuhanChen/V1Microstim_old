package org.xper.sach.vo;


import com.thoughtworks.xstream.XStream;

public class ExpLog_RecLogMsg {
	
	String xperVersion;				
	String dbHost;
	String dbName;
	String dbUser;
	String dbPswd;
	String animalID;
	String animalHemisphere;
	String animalTargetArea;
	String electrodeMake;
	String electrodeSpecs;
	
	double[] coordsBV;
	double[] coordsST;
	double[] coordsAS;
	double elev;
	double azim;
	double dist;
	
	
	public ExpLog_RecLogMsg(String xperVersion,
			String dbHost,String dbName,String dbUser,String dbPswd,
			String animalId,String animalHemisphere,String animalTargetArea,
			String electrodeMake,String electrodeSpecs,
			double[] targetCoords_BV,double[] targetCoords_ST,double[] targetCoords_AS,
			double targetElev,double targetAzim,double targetDist) 
	{
		super();
		setXperVersion(xperVersion);
		setDbHost(dbHost);
		setDbName(dbName);
		setDbUser(dbUser);
		setDbPswd(dbPswd);
		setAnimalID(animalId);
		setAnimalHemisphere(animalHemisphere);
		setAnimalTargetArea(animalTargetArea);
		setElectrodeMake(electrodeMake);
		setElectrodeSpecs(electrodeSpecs);
		setCoordsBV(targetCoords_BV);
		setCoordsST(targetCoords_ST);
		setCoordsAS(targetCoords_AS);
		setElev(targetElev);
		setAzim(targetAzim);
		setDist(targetDist);
		
	}
	
	public ExpLog_RecLogMsg(String xperVersion,
			String dbHost,String dbName,String dbUser,String dbPswd,
			String animalId,String animalHemisphere,String animalTargetArea,
			String electrodeMake,String electrodeSpecs,
			String targetCoords_BV,String targetCoords_ST,String targetCoords_AS,
			double targetElev,double targetAzim,double targetDist) 
	{
		super();
		setXperVersion(xperVersion);
		setDbHost(dbHost);
		setDbName(dbName);
		setDbUser(dbUser);
		setDbPswd(dbPswd);
		setAnimalID(animalId);
		setAnimalHemisphere(animalHemisphere);
		setAnimalTargetArea(animalTargetArea);
		setElectrodeMake(electrodeMake);
		setElectrodeSpecs(electrodeSpecs);
		setCoordsBV(str2double(targetCoords_BV));
		setCoordsST(str2double(targetCoords_ST));
		setCoordsAS(str2double(targetCoords_AS));
		setElev(targetElev);
		setAzim(targetAzim);
		setDist(targetDist);
		
	}

	public ExpLog_RecLogMsg(String xperVersion, double targetDist) 
	{
		super();
		setXperVersion(xperVersion);
		setDist(targetDist);
		
	}
	
	public ExpLog_RecLogMsg(String xperVersion, String dbHost) 
	{
		super();
		setXperVersion(xperVersion);
		setDbHost(dbHost);		
	}
	
	static XStream xstream = new XStream();

	static {
		xstream.alias("ExpLog_RecLogMsg", ExpLog_RecLogMsg.class);
	}
	
	public static ExpLog_RecLogMsg fromXml(String xml) {
		return (ExpLog_RecLogMsg)xstream.fromXML(xml);
	}
	
	public static String toXml(ExpLog_RecLogMsg msg) {
		return xstream.toXML(msg);
	}
	
	double[] str2double(String s) {
		String[] split = s.split("\\s+");
		double[] nums = new double[split.length];
		for (int n=0;n<nums.length;n++) {
			if (isNumeric(split[n]))
				nums[n] = Double.parseDouble(split[n]);			
		}
		
		return nums;
	}
	
	boolean isNumeric(String s) {
		try
		{
			double d = Double.parseDouble(s);
		}
		catch(NumberFormatException nfe)
		{
			return false;
		}
		return true;
	}

	public String getXperVersion() {
		return xperVersion;
	}

	public void setXperVersion(String xperVersion) {
		this.xperVersion = xperVersion;
	}

	public String getDbHost() {
		return dbHost;
	}

	public void setDbHost(String dbHost) {
		this.dbHost = dbHost;
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public String getDbUser() {
		return dbUser;
	}

	public void setDbUser(String dbUser) {
		this.dbUser = dbUser;
	}

	public String getDbPswd() {
		return dbPswd;
	}

	public void setDbPswd(String dbPswd) {
		this.dbPswd = dbPswd;
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

	public String getElectrodeMake() {
		return electrodeMake;
	}

	public void setElectrodeMake(String electrodeMake) {
		this.electrodeMake = electrodeMake;
	}

	public String getElectrodeSpecs() {
		return electrodeSpecs;
	}

	public void setElectrodeSpecs(String electrodeSpecs) {
		this.electrodeSpecs = electrodeSpecs;
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

	public double getElev() {
		return elev;
	}

	public void setElev(double elev) {
		this.elev = elev;
	}

	public double getAzim() {
		return azim;
	}

	public void setAzim(double azim) {
		this.azim = azim;
	}

	public double getDist() {
		return dist;
	}

	public void setDist(double dist) {
		this.dist = dist;
	}
}
