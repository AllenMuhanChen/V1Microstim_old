package org.xper.sach.vo;

import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachIOUtil;

import com.thoughtworks.xstream.XStream;

public class SachExpLogMessage {
	String status;				// start, stop, gen_done, ... ?
	String trialType;			// which trial type (ga, beh, etc) ?
	long depth = -1;			// electrode depth, if applicable
	long genNum = -1;			// which generation?
	long globalGenId = -1;		// this is the genId in TaskToDo db table
	long firstGenId = -1; 		// only applicable for GA gens: genId of the first generation
	boolean realExp;			// is this a real or mock expt?
	String dateTime;			// readable date/time string
	long timestamp;
	long cellNum = -1;			// cell number as YYYYMMDDXXX

	
//	public SachExpLogMessage(String status, String trialType, long genNum, long globalGenId, boolean realExp, long timestamp) {
//		super();
//		setStatus(status);
//		setTrialType(trialType);
//		setGenNum(genNum);
//		setGlobalGenId(globalGenId);
//		setRealExp(realExp);
//		setTimestamp(timestamp);
//	}
//	public SachExpLogMessage(String status, String trialType,long depth, long genNum, long globalGenId, boolean realExp, long timestamp) {
//		super();
//		setStatus(status);
//		setTrialType(trialType);
//		setDepth(depth);
//		setGenNum(genNum);
//		setGlobalGenId(globalGenId);
//		setRealExp(realExp);
//		setTimestamp(timestamp);
//	}
	public SachExpLogMessage(String[] dbInputs){
		// AWC August2017. This constructor is meant to be called after querying the database and returning a String[]. The order of data in the String[] will exactly match the column names in ExpLog table, 
		//    so ANY CHANGES MADE TO ExpLog STRUCTURE MUST BE REFLECTED IN THE INPUTS OF THIS FUNCTION
		super();
		
		String tstampS = 	dbInputs[0];
		// String dateTimeS.dbInputs[1]
		String statusS = 	dbInputs[2];
		String typeS = 		dbInputs[3];
		String depthS = 	dbInputs[4];
		String globalGenIdS=dbInputs[5];
		String localGenIdS =dbInputs[6];
		String firstGlobalGenIdS = dbInputs[7];
		String cellNumS = 	dbInputs[8];
		String isRealExptS =dbInputs[9];
//		String memoS........dbInputs[10]
				
		setStatus(statusS);
		setTrialType(typeS);
		setDepth(Long.parseLong(depthS));
		setGenNum(Long.parseLong(localGenIdS));
		setGlobalGenId(Long.parseLong(globalGenIdS));
		setFirstGenId(Long.parseLong(firstGlobalGenIdS));
		setCellNum(Long.parseLong(cellNumS));
		setRealExp(Boolean.parseBoolean(isRealExptS));
		setTimestamp(Long.parseLong(tstampS));
	}
	public Object[] dbObjectList(){
//		return new String[] {Long.toString(getTimestamp()),getDateTime(),getStatus(),getTrialType(),Long.toString(getDepth()),Long.toString(getGlobalGenId()),Long.toString(getGenNum()),
//				             Long.toString(getFirstGenNum()),Long.toString(getCellNum()),Boolean.toString(getRealExp()),SachExpLogMessage.toXml(this)};
		return new Object[] {getTimestamp(),SachDbUtil.addQ(getDateTime()),SachDbUtil.addQ(getStatus()),SachDbUtil.addQ(getTrialType()),
				             getDepth(),getGlobalGenId(),getGenNum(),getFirstGenNum(),getCellNum(),getRealExp(),1,SachDbUtil.addQ(SachExpLogMessage.toXml(this))};

	}
	
	public SachExpLogMessage(String status, String trialType,long depth, long genNum, long globalGenId, long firstGenId, long cellNum, boolean realExp, long timestamp) {
		super();
		setStatus(status);
		setTrialType(trialType);
		setDepth(depth);
		setGenNum(genNum);
		setGlobalGenId(globalGenId);
		setFirstGenId(firstGenId);
		setCellNum(cellNum);
		setRealExp(realExp);
		setTimestamp(timestamp);
	}
	
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
		this.dateTime = SachIOUtil.formatMicroSeconds(timestamp);
	}
	public String getDateTime() {
		return dateTime;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTrialType() {
		return trialType;
	}
	public void setTrialType(String trialType) {
		this.trialType = trialType;
	}
	public long getGenNum() {
		return genNum;
	}
	public long getFirstGenNum() {
		return firstGenId;
	}
	public void setGenNum(long genNum) {
		this.genNum = genNum;
	}
	public long getGlobalGenId() {
		return globalGenId;
	}
	public void setGlobalGenId(long genId) {
		this.globalGenId = genId;
	}
	public void setFirstGenId(long firstGenId) {
		this.firstGenId = firstGenId;
	}
	public boolean getRealExp() {
		return realExp;
	}
	public void setRealExp(boolean realExp) {
		this.realExp = realExp;
	}

	public long getDepth() {
		return depth;
	}
	public void setDepth(long depth) {
		this.depth = depth;
	}

	
	public long getCellNum() {
		return cellNum;
	}
	public void setCellNum(long cellNum) {
		this.cellNum = cellNum;
	}


	static XStream xstream = new XStream();

	static {
		xstream.alias("SachExpLogMessage", SachExpLogMessage.class);
	}
	
	public static SachExpLogMessage fromXml (String xml) {
		return (SachExpLogMessage)xstream.fromXML(xml);
	}
	
	public static String toXml (SachExpLogMessage msg) {
		return xstream.toXML(msg);
	}
}
