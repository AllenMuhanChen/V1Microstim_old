package org.xper.db.vo;

public class TaskDoneEntry {
	long tstamp;
	long taskId;
	int part_done;
	
	public TaskDoneEntry(){
		super();
	}
	
	public TaskDoneEntry(String[] dbInputs){
		// AWC August2017. This constructor is meant to be called after querying the database and returning a String[]. The order of data in the String[] will exactly match the column names in ExpLog table, 
		//    so ANY CHANGES MADE TO ExpLog STRUCTURE MUST BE REFLECTED IN THE INPUTS OF THIS FUNCTION
		super();
		
		String tstampS = 	dbInputs[0];
		String taskIdS =	dbInputs[1];
		String part_doneS =	dbInputs[2];
		setTstamp(Long.parseLong(tstampS));
		setTaskId(Long.parseLong(taskIdS));
		setPart_done(Integer.parseInt(part_doneS));
	}
	
	public int getPart_done() {
		return part_done;
	}
	public void setPart_done(int part_done) {
		this.part_done = part_done;
	}
	public long getTaskId() {
		return taskId;
	}
	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}
	public long getTstamp() {
		return tstamp;
	}
	public void setTstamp(long tstamp) {
		this.tstamp = tstamp;
	}
}
