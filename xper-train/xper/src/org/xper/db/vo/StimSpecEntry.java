package org.xper.db.vo;

public class StimSpecEntry {
	/**
	 * It's the timestamp in microseconds.
	 */
	long stimId;
	/**
	 * Encoded as XML string.
	 */
	String spec;
	
	public StimSpecEntry(){
		super();
	}
	public StimSpecEntry(String[] dbInput,String type){
		super();
		
		String stimIdS = dbInput[0];
		String specS;
		if (type.toLowerCase().equals("spec"))	specS = dbInput[1];
		else 									specS = dbInput[2];
		setStimId(Long.parseLong(stimIdS));
		setSpec(specS);
	}
	
	public String getSpec() {
		return spec;
	}
	public void setSpec(String spec) {
		this.spec = spec;
	}
	public long getStimId() {
		return stimId;
	}
	public void setStimId(long stimId) {
		this.stimId = stimId;
	}
}
