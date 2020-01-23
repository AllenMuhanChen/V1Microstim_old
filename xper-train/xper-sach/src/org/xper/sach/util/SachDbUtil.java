package org.xper.sach.util;


import java.io.File; 
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.TreeMap;

import javax.sql.DataSource;
//import com.mchange.v2.c3p0.ComboPooledDataSource;

import java.awt.image.DataBufferByte;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.beans.PropertyVetoException;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowCallbackHandler;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.xper.db.vo.BehMsgEntry;
import org.xper.db.vo.ExpLogEntry;
import org.xper.db.vo.GenerationTaskDoneList;
import org.xper.db.vo.StimSpecEntry;
import org.xper.db.vo.TaskDoneEntry;
import org.xper.exception.DbException;
import org.xper.sach.analysis.SachStimDataEntry;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
import org.xper.sach.drawing.stimuli.OccluderManifoldSpec;
//import org.xper.sach.expt.SachExptSpec;
import org.xper.sach.vo.SachExpLogMessage;
import org.xper.sach.vo.SachTrialOutcomeMessage;
import org.xper.util.DbUtil;

import com.mchange.v2.c3p0.ComboPooledDataSource;


public class SachDbUtil extends DbUtil {

//	@Dependency
//	ComboPooledDataSource dataSource2;
	
	public SachDbUtil() {	
	}
	
	public SachDbUtil(DataSource dataSource) {
		super();
		this.dataSource = dataSource;
	}

	/**
	 * Before DbUtil can be used. DataSource must be set.
	 * 
	 * See createXperDbUtil in MATLAB directory for how to create data source.
	 * 
	 * @param dataSource
	 */
	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	
	// *********************General Purpose Helper functions: input strings ==> SQL query 
	public String processSqlSelectString(String select){
		String[] selectSep = select.split(";");
		if (selectSep.length==1) return select;
		else {
			String q;
			if (selectSep[0].equals("extract")){
				q = "ExtractValue(" + selectSep[1] + ",'";
				for (int ss=2;ss<selectSep.length;ss++)
					q = q + "/" + selectSep[ss];
				q = q + "')";
			}
			else {
				q = "case";
				int ss = 0;
				while (ss<selectSep.length){
					if (ss==selectSep.length-1) 	q = q + " else " + selectSep[ss];
					else  							q = q + " when " + selectSep[ss] + " then " + selectSep[ss+1];
					ss = ss + 2;
				}
				q = q + " end";
			}
			return q;
		}
	}
	
	public String processSqlString(String table, String select,String[] cond){
		String processCond = processSqlCondString(cond,table);
		
		return "select " + processSqlSelectString(select) + " from " +  table + " " + processCond;
	}
	public String processSqlCondString(String[] cond, String table){
		if (cond.length==0) 
			return "order by " + readTablePrimaryColumn(table) + " desc limit 1";
		
		String q = "";
		boolean whereFlag = false;
		for (String curInp : cond){
			String[] curSep = curInp.split(",");
			if (curSep[0].equals("order")){
				q = q + " order by ";
				if ((curSep.length==1) || (curSep[1].equals("desc"))) 	q = q + readTablePrimaryColumn(table);
				else  													q = q + curSep[1];
		
				if (curSep[curSep.length-1].equals("desc")) q = q + " desc";
			}
			else if (curSep[0].equals("limit")){
				q = q + " limit ";
				if (curSep.length>1) 	q = q + curSep[1];
				else  					q = q + "1";
			}
			else if (curSep[0].equals("odl"))  	q = q + " order by " + readTablePrimaryColumn(table) + " desc limit 1";
			else if (curSep[0].equals("ol"))    q = q + " order by " + readTablePrimaryColumn(table) + " limit 1";
			else if (!curSep[0].equals("odl") && !curSep[0].equals("ol")){
				if (!whereFlag) {
					q = q + " where ";
					whereFlag = true;
				}
				else q = q + " and ";
				
				if (curSep.length==1) 	q = q + " " + readTablePrimaryColumn(table) + "=" + curSep[0];
				else{
					q = q + curSep[0];
					if (curSep.length==2) 					q = q + "=";
					for (int cs=1;cs<curSep.length;cs++) 	q = q + curSep[cs];
				}
			}
		}
		return q;
	}
	
	public static String addQ(String inp) {return "'" + inp + "'";}
	public String[] addQ(String[] inp){
		String[] sQ = new String[inp.length];
		for (int ss=0 ; ss<inp.length;ss++) sQ[ss] = addQ(inp[ss]);
		return sQ;
	}
	
	//*********************General Purpose Helper functions: meta info about database************
	public void resetSource(){
		ComboPooledDataSource source = new ComboPooledDataSource();
		try {
			source.setDriverClass("com.mysql.jdbc.Driver");
		} catch (PropertyVetoException e) {
			throw new DbException(e);
		}
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/test1");
		String url;
		try {
			url = dataSource.getConnection().getMetaData().getURL();
			source.setJdbcUrl(url);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/jk_test");
//		source.setJdbcUrl("jdbc:mysql://172.30.6.48/sach_ecpc48_2014_08_12_recording");
//		source.setJdbcUrl("jdbc:mysql://172.30.4.9/sach_ecpc48_2014_08_12_recording_20161122");
		source.setUser("xper_rw");
		source.setPassword("up2nite");
		setDataSource(source);
	}
	
	public String getDBSchema(){
//		String urlAll = dataSource2.getJdbcUrl();
		String urlAll;
		try {
			urlAll = dataSource.getConnection().getMetaData().getURL();
			String[] urlSep = urlAll.split("/");
			return urlSep[urlSep.length-1];
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			urlAll = "shaggy_ecpc48_2016_07";
			return urlAll;
		}
	}
	
	public List<String> readTableColumnNames(String table){
		List<String> out = new ArrayList<String>();
		if (table.equals("BehMsg")){
			out.add("tstamp");out.add("type");out.add("msg");
		}
		else if (table.equals("CanonicalThumbnail")){
			out.add("id");out.add("data");
		}
		else if (table.equals("ExpLog")){
			out.add("tstamp");out.add("dateTime");out.add("status");out.add("type");out.add("depth");out.add("globalGenId");out.add("localGenId");
			out.add("firstGlobalGenId");out.add("cellNum");out.add("isRealExpt");out.add("goodRun");out.add("memo");
		}
		else if (table.equals("ExpRecTargets")){
			out.add("cellNum");out.add("drivenDistance");out.add("coordsST_ML");out.add("coordsST_AP");out.add("coordsST_DV");out.add("coordsAS_azim");out.add("coordsAS_elev");
			out.add("coordsAS_dist");out.add("notes");out.add("isFinished");
		}
		else if (table.equals("GenId_to_StimObjId")){
			out.add("gen_id");out.add("stimObjId");
		}
		else if (table.equals("InternalState")){
			out.add("name");out.add("arr_ind");out.add("val");
		}
		else if (table.equals("SessionLog")){
			out.add("dateTime");out.add("drivenDistance");out.add("pivotDistance");out.add("guideTubeDistance");out.add("coordsST_ML");
			out.add("coordsST_AP");out.add("coordsST_DV");out.add("coordsAS_azim");out.add("coordsAS_elev");out.add("coordsAS_dist");out.add("notes");
		}
		else if (table.equals("StimObjData")){
			out.add("id");out.add("spec");out.add("data");
		}
		else if (table.equals("StimSpec")){
		out.add("id");out.add("spec");
		}
		else if (table.equals("SystemVar")){
		out.add("name");out.add("arr_ind");out.add("tstamp");out.add("val");
		}
		else if (table.equals("TaskDone")){
		out.add("tstamp");out.add("task_id");out.add("part_done");
		}
		else if (table.equals("TaskToDo")){
			out.add("task_id");out.add("stim_id");out.add("xfm_id");out.add("gen_id");
		}
		else if (table.equals("Thumbnail")){
			out.add("id");out.add("data");
		}
		return out;
	}
	public String readTablePrimaryColumn(String table){
		List<String> cols = readTableColumnNames(table);
		return cols.get(0);
	}
//	public List<String> readTableColumnNames(String table){
//		String q = "select column_name from information_schema.COLUMNS WHERE TABLE_NAME = '" + table + "' and table_schema='" + getDBSchema() + "'";
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		
//		final List<String> s = new ArrayList<String>();
//		jt.query(q, new Object[]{},
//				new RowCallbackHandler() {
//			public void processRow(ResultSet rs) throws SQLException {
//				s.add(rs.getString("column_name"));
//			}});
//		return s;
////		String[] sa = new String[s.size()];
////		sa = s.toArray(sa);
////		return sa;
//	}
//	
//	public String readTablePrimaryColumn(String table){
//		String q = "select column_name from information_schema.COLUMNS WHERE TABLE_NAME = '" + table + "' and table_schema='" + getDBSchema() + "' and column_key='pri'";
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		
//		final List<String> s = new ArrayList<String>();
//		jt.query(q, new Object[]{},
//				new RowCallbackHandler() {
//			public void processRow(ResultSet rs) throws SQLException {
//				s.add(rs.getString("column_name"));
//			}});
//		return s.get(0);
//	}
	
	// *********************General Purpose Helper functions:  etc******************
	public String[] toArray(List<String> s){
		String[] sa = new String[s.size()];
		sa = s.toArray(sa);
		return sa;
	}
	public long[] toArray(List<Long> l){
		Long[] la = new Long[l.size()];
		la = l.toArray(la);
		long[] la_p = new long[la.length];
		for (int aa=0;aa<la.length;aa++)
			la_p[aa] = la[aa];
		return la_p;
	}
	public double[] toArray(List<Double> d){
		Double[] da = new Double[d.size()];
		da = d.toArray(da);
		double[] da_p = new double[da.length];
		for (int aa=0;aa<da.length;aa++)
			da_p[aa] = da[aa];
		return da_p;
	}
	public String[] sArray(String s) {
		return new String[]{s};
	}
	public String[] sArray(long l) {
		return new String[]{Long.toString(l)};
	}
	public String[] sArray(double d) {
		return new String[]{Double.toString(d)};
	}
	
	// *********************General Purpose DB Read Functions**********************************
	public String readString(String table, String select,String[] cond){
		String q = processSqlString(table,select,cond);
		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
		return jt.queryForObject(q, String.class);
	}
	public long readLong(String table, String select,String[] cond){
		String s = readString(table,select,cond);
		long out = Long.parseLong(s); 
		return out;
	}
	public double readDouble(String table, String select,String[] cond){
		String s = readString(table,select,cond);
		double out = Double.parseDouble(s);
		return out;
	}
	public int readInteger(String table, String select,String[] cond){
		String s = readString(table,select,cond);
		int out = Integer.parseInt(s);
		return out;
	}
	
	public List<String> readRowsString(String table, String select,String[] cond){
		// IMPORTANT: for now it is assumed that select is a SIMPLE input (not "case" inputs)
		String q = processSqlString(table,select,cond);
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		
		final ArrayList<String> s = new ArrayList<String>();
		final String finalSelect = select;
		jt.query(q, //new Object[]{},
				new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				s.add(rs.getString(finalSelect));
			}});

		return s;
	}
	public List<Long> readRowsLong(String table, String select,String[] cond){
		List<String> s = readRowsString(table,select,cond);
		List<Long> l = new ArrayList<Long>();
		for (String ss : s) l.add(Long.parseLong(ss));
		return l;
	}
	public List<Double> readRowsDouble(String table, String select,String[] cond){
		List<String> s = readRowsString(table,select,cond);
		List<Double> d = new ArrayList<Double>();
		for (String ss : s) d.add(Double.parseDouble(ss));
		return d;
	}
	

	// 				******** duplicate, but w/o last cond argument**********
	public String readString(String table, String select){
		return readString(table,select,new String[]{});
	}
	public long readLong(String table, String select){
		return readLong(table,select,new String[]{});
	}
	public double readDouble(String table, String select){
		return readDouble(table,select,new String[]{});
	}
	public int readInteger(String table, String select){
		return readInteger(table,select,new String[]{});
	}
	public List<String> readRowsString(String table, String select){
		return readRowsString(table,select,new String[]{});
	}
	public List<Long> readRowsLong(String table, String select){
		return readRowsLong(table,select,new String[]{});
	}
	public List<Double> readRowsDouble(String table, String select){
		return readRowsDouble(table,select,new String[]{});
	}
	// 			********************
	
	public List<String> readColsString(String table,String[] cond){
		return readColsString(table,toArray(readTableColumnNames(table)), cond);
	}
	public List<String> readColsString(String table, final String[] colNames, String[] cond){
		String select = "*";
		String q = processSqlString(table,select,cond);
		JdbcTemplate jt = new JdbcTemplate(dataSource);

//		final String[] colNames = toArray(readTableColumnNames(table));
		final ArrayList<String> colVals = new ArrayList<String>();
		jt.query(q, 
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						for (int ss=0;ss<colNames.length;ss++) colVals.add(rs.getString(colNames[ss]));
					}});
		
		return colVals;
	}
	
	public byte[] readBytes(String table,String select,String[] cond){
		String q = processSqlString(table,select,cond);
		final String finalSelect = select;
		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
		return jt.queryForObject(q,
				new ParameterizedRowMapper<byte[]>() {
					public byte[] mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						return rs.getBytes(finalSelect);
					}},
				new Object[]{});
	}
	
	
	// *********************General Purpose DB Write Functions***********************************
	public void updateLine(String table,String field,Object data,String[] cond){
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		jt.update("update " + table + " set " + field + "=? " +  processSqlCondString(cond,table),new Object[] {data});
	}
	public void updateLine(String table,String field,Object[] data,String[] cond){
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		jt.update("update " + table + " set " + field + "=? " +  processSqlCondString(cond,table),data);
	}
	
	
	
	public void writeLine(String table, Object[] data){
		String[] fields = toArray(readTableColumnNames(table));
		writeLine(table,fields,data);
	}
	public void writeLine(String table, Object[] data,boolean isByte){
		String[] fields = toArray(readTableColumnNames(table));
		writeLine(table,fields,data,isByte);
	}
	public void writeLine(String table, String[] fields, Object[] data){
		writeLine(table,fields,data,false);
	}
	public void writeLine(String table, String[] fields, Object[] data,boolean isByte){
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		String f = "";
		String d = "";
		
//		boolean isByte =  (data.length==2 && (data[1] instanceof Byte));
		
		for (int cc=0;cc<fields.length;cc++) {
			f = f + fields[cc];
			if (isByte) d = d + "?";
			else 		d = d + data[cc];
			if (cc!=fields.length-1) {
				f = f + ", ";
				d = d + ", ";
			}
		}
		String q = "insert into " + table + " (" + f + ") values (" + d + ")";
		if (isByte) jt.update(q,data);
		else        jt.update(q);
	}
	
	//******************* Sort Functions**********************************//
	public int[] sortIndex(double[] vals){
		return sortIndex(vals,true);
	}
		
	public int[] sortIndex(final double[] vals,final boolean ascend){
		Integer[] index = new Integer[vals.length];
		for (int vv=0;vv<vals.length;vv++)
			index[vv] = vv;
		Arrays.sort(index,new Comparator<Integer>() {
    		@Override
    		public int compare(final Integer i1, final Integer i2){
    			int comp = (int)Math.signum(vals[i1]-vals[i2]);
    			if (!ascend)
    				comp = -comp;
    			return comp;
    		}
		});
		
		int[] index_p = new int[index.length];
		for (int ii=0;ii<index.length;ii++)
			index_p[ii] = index[ii];
		return index_p;
	}
	
	public double[] applyIndex(double[] inp,int[] index){
		double[] out = new double[inp.length];
		for (int oo=0;oo<inp.length;oo++)
			out[oo] = inp[index[oo]];
		return out;
	}
	public long[] applyIndex(long[] inp,int[] index){
		long[] out = new long[inp.length];
		for (int oo=0;oo<inp.length;oo++)
			out[oo] = inp[index[oo]];
		return out;
	}
	public int[] applyIndex(int[] inp,int[] index){
		int[] out = new int[inp.length];
		for (int oo=0;oo<inp.length;oo++)
			out[oo] = inp[index[oo]];
		return out;
	}
	
	//******************  Behavior Performance Functions*****************//
	// read last Trial Outcome from Beh Msg -shs
//	public BehMsgEntry readBehMsgMaxTrialOutcome() {
//		return new BehMsgEntry(toArray(readColsString("BehMsg",new String[]{"type,'TrialOutcome'" , "odl"})));
////		JdbcTemplate jt = new JdbcTemplate(dataSource);
////
////		final ArrayList<BehMsgEntry> result = new ArrayList<BehMsgEntry>();
////		jt.query("SELECT @maxtstamp:=max(tstamp) FROM BehMsg WHERE type = 'TrialOutcome';" +
////				"SELECT tstamp,type,msg FROM BehMsg " + 
////				"WHERE type = 'TrialOutcome' AND tstamp = @maxtstamp;", 
////				new RowCallbackHandler() {
////					public void processRow(ResultSet rs) throws SQLException {
////						BehMsgEntry ent = new BehMsgEntry();
////						ent.setTstamp(rs.getLong("tstamp")); 
////						ent.setType(rs.getString("type")); 
////						ent.setMsg(rs.getString("msg")); 
////
////						result.add(ent);
////					}});
////		
////		return result.get(0);
//	}
	
	// use this one
//	public BehMsgEntry getLastTrialOutcome() {
//		return new BehMsgEntry(toArray(readColsString("BehMsg",new String[]{"type,'TrialOutcome'" , "odl"})));
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		long tstamp = jt.queryForLong("SELECT max(tstamp) FROM BehMsg WHERE type='TrialOutcome'");
////		return jt.queryForObject(
////				"SELECT tstamp,type,msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp=?",
////				new ParameterizedRowMapper<BehMsgEntry>() {
////					public BehMsgEntry mapRow(ResultSet rs, int rowNum)
////							throws SQLException {
////						BehMsgEntry ent = new BehMsgEntry();
////						
////						ent.setType(rs.getString("type"));
////						ent.setTstamp(rs.getLong("tstamp")); 
////						ent.setMsg(rs.getString("msg")); 
////
////						return ent;
////					}},
////					new Object[]{tstamp});
//	}

//	public String getLastTrialOutcomeMsg() {
//		return readString("BehMsg","msg",new String[]{"type,TrialOutcome" , "odl"});
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		long tstamp = jt.queryForLong("SELECT max(tstamp) FROM BehMsg WHERE type='TrialOutcome'");
////		return jt.queryForObject(
////				"SELECT msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp=?",
////				new ParameterizedRowMapper<String>() {
////					public String mapRow(ResultSet rs, int rowNum)
////							throws SQLException {
////						return rs.getString("msg");
////					}},
////				new Object[]{tstamp});
//	}

//	public String getTrialOutcomeByTstamp(long timestamp) {
//		return readString("BehMsg","msg",new String[]{"type,TrialOutcome" , "tstamp," + timestamp});
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		return jt.queryForObject(
////				"SELECT msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp=?",
////				new ParameterizedRowMapper<String>() {
////					public String mapRow(ResultSet rs, int rowNum)
////							throws SQLException {
////						return rs.getString("msg");
////					}},
////				new Object[]{timestamp});
//	}
	
//	public String getTaskIdOutcomeByTaskId(long taskId) {
//		return readString("BehMsg","msg",new String[]{"type,TaskIdOutcome" , "tstamp," + taskId});
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		return jt.queryForObject(
////				"SELECT msg FROM BehMsg WHERE type='TaskIdOutcome' AND tstamp=?",
////				new ParameterizedRowMapper<String>() {
////					public String mapRow(ResultSet rs, int rowNum)
////							throws SQLException {
////						return rs.getString("msg");
////					}},
////				new Object[]{taskId});
//	}
	
	
	// Get trial outcome given the taskId:

	// readBehMsgTrialStart given a time stamp during the trial (TaskDone tstamp)
	// read last Trial Outcome from Beh Msg -shs	
	public long readBehMsgTrialStart(long tstamp) {
		return readLong("BehMsg","tstamp",new String[]{"type,TrialStart" , "tstamp,<," + tstamp , "odl"});
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT tstamp FROM BehMsg WHERE type='TrialStart' AND tstamp < ? ORDER BY tstamp DESC LIMIT 1";
//		return jt.queryForLong(q,tstamp);
	}

	// read TrialOutcome msg given TrialStart tstamp
	public String readTrialOutcomeByTrialStartTime(long tstamp) {
		return readString("BehMsg","msg",new String[]{"type,TrialOutcome" , "tstamp,>," + tstamp , "ol"});
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp > ? ORDER BY tstamp LIMIT 1";
//		return jt.queryForObject(q, String.class, tstamp);
	}
	
//	public String readTrialOutcomeByTaskDoneTime(long tstamp) {
//		return readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
//	}

	// in order to check that a certain TrialOutcome belongs to a certain taskId, we need to verify this
	public String readTrialOutcomeByTaskId(long taskId) {
		long tstamp = readTaskDoneTimeLast(taskId);
		String trialOutcomeString = readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
		SachTrialOutcomeMessage msg = SachTrialOutcomeMessage.fromXml(trialOutcomeString);
		String trialOutcome = msg.getOutcome();
		
		// checking if trialOutcome matches taskId
		long taskIdCheck = msg.getTaskID();
		if (taskIdCheck != taskId) {
//			JK		trialOutcome = "N/A";
					trialOutcome = "NA";
		} 
				
		return trialOutcome;
	}

	
	// read TrialOutcome msg nearest a given tstamp
		public List<String> readTrialOutcomeNearestTimeStamp(long tstamp) {
			return readRowsString("BehMsg","msg",new String[] {"type,'TrialOutcome'" , "tstamp,>," + (tstamp-100000000) , "order,tstamp" , "limit,5"});
			
//			final ArrayList<String> res = new ArrayList<String>();
//			JdbcTemplate jt = new JdbcTemplate(dataSource);
//			String q = "SELECT b.msg AS msg FROM (SELECT * FROM BehMsg WHERE (type='TrialOutcome' AND tstamp > (?-100000000))) AS b ORDER BY ABS(b.tstamp-?) LIMIT 5";
////			String q = "SELECT msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp > ? ORDER BY tstamp LIMIT 1";
////			return jt.queryForObject(q, String.class, tstamp,tstamp);
//
//			jt.query(q, new Object[] { tstamp,tstamp },
//					new RowCallbackHandler() {
//				public void processRow(ResultSet rs) throws SQLException {
//					res.add(rs.getString("msg"));
//				}});
//
//			return res;
		}
		
	// JK 13 April 2016
	// This method isn't currently used but the string "N/A" has caused problems
	// and may need to be changed to "NA"
	public SachTrialOutcomeMessage readTrialOutcomeMsgByTaskId2(long taskId) {
		long tstamp = readTaskDoneTimeLast(taskId);
		
		List<String> s = readTrialOutcomeNearestTimeStamp(tstamp);
		String trialOutcome = "N/A";
		SachTrialOutcomeMessage msg = null;
		int c = 0;
		
		while ((trialOutcome.matches("N/A")) && (c < s.size())) {
			
			msg = SachTrialOutcomeMessage.fromXml(s.get(c));

			// checking if trialOutcome matches taskId
			long taskIdCheck = msg.getTaskID();
			if (taskIdCheck != taskId) {
				trialOutcome = "N/A";
				msg.setOutcome(trialOutcome);
			} else {
				trialOutcome = msg.getOutcome();
			}
			
			c = c+1;
		}
		
		return msg;
	}
	
//	public SachTrialOutcomeMessage readTrialOutcomeMsgByTaskId(long taskId) {
//		long tstamp = readTaskDoneTimeLast(taskId);
//		String trialOutcomeString = readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
//		SachTrialOutcomeMessage msg = SachTrialOutcomeMessage.fromXml(trialOutcomeString);
//		String trialOutcome = msg.getOutcome();
//		
//		// checking if trialOutcome matches taskId
//		long taskIdCheck = msg.getTaskID();
//		if (taskIdCheck != taskId) {
////			JK		trialOutcome = "N/A";
//					trialOutcome = "NA";
//			msg.setOutcome(trialOutcome);
//		} 
//		
//		return msg;
//	}
	
	
	public long readTaskDoneTimeLast(long taskId) {
		// last one should have part_done = 0 (i.e. it was most likely completed)
		return readLong("TaskDone","tstamp",new String[]{Long.toString(taskId) , "odl"});
		
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String q = "SELECT tstamp FROM TaskDone WHERE task_id = ? ORDER BY tstamp DESC LIMIT 1";
//		return jt.queryForLong(q, new Object[] { new Long(taskId) });
	}
	
	
	
	/**
	 * Get only the latest taskDone times for each TaskId in a generation. -SHS
	 * 
	 * @param genId
	 * @return {@link GenerationTaskDoneList} empty if there is no done tasks
	 *         for the generation in database.
	 */

	public GenerationTaskDoneList readTaskDoneByGenerationLatest(long genId) {
		final GenerationTaskDoneList taskDone = new GenerationTaskDoneList();
		taskDone.setGenId(genId);
		taskDone.setDoneTasks(new ArrayList<TaskDoneEntry>());
		
		
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		final String[] colNames = toArray(readTableColumnNames("TaskDone"));
		final String[] colVals = new String[colNames.length];
		final List<String[]> dbInputs = new ArrayList<String[]>();  
		jt.query("SELECT d.tstamp AS tstamp, d.task_id AS task_id, d.part_done AS part_done " +
				"FROM TaskDone d, TaskToDo t " +
				"WHERE t.gen_id = ? AND d.task_id = t.task_id AND d.tstamp IN " +
				"(SELECT max(tstamp) AS max_tstamp FROM TaskDone GROUP BY task_id) " + 
				"ORDER BY d.tstamp", new Object[] {genId},
				new RowCallbackHandler() {
					public void processRow(ResultSet rs) throws SQLException {
						for (int ss=0;ss<colNames.length;ss++) colVals[ss] =rs.getString(colNames[ss]);
						dbInputs.add(colVals);
					}});
		
		for (String[] di : dbInputs) taskDone.getDoneTasks().add(new TaskDoneEntry(di));
		return taskDone;
		
		
//		final GenerationTaskDoneList taskDone = new GenerationTaskDoneList();
//		taskDone.setGenId(genId);
//		taskDone.setDoneTasks(new ArrayList<TaskDoneEntry>());
//		
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String q = 	"SELECT d.tstamp AS tstamp, d.task_id AS task_id, d.part_done AS part_done " +
//					"FROM TaskDone d, TaskToDo t " +
//					"WHERE t.gen_id = ? AND d.task_id = t.task_id AND d.tstamp IN " +
//					"(SELECT max(tstamp) AS max_tstamp FROM TaskDone GROUP BY task_id) " + 
//					"ORDER BY d.tstamp";
//		jt.query(q, new Object[] { genId },
//			new RowCallbackHandler() {
//				public void processRow(ResultSet rs) throws SQLException {
//					TaskDoneEntry ent = new TaskDoneEntry();
//					ent.setTaskId(rs.getLong("task_id")); 
//					ent.setTstamp(rs.getLong("tstamp")); 
//					ent.setPart_done(rs.getInt("part_done"));
//					taskDone.getDoneTasks().add(ent);
//				}});
//		return taskDone;
		
	}
	
//	public List<Long> readTaskDoneTimes(long genId) {
//		return 	readRowsLong("TaskDone d, TaskToDo t","d.tstamp",new String[] {"d.task_id,t.task_id" , "t.gen_id,5100"});
//
////		JdbcTemplate jt = new JdbcTemplate(dataSource);
////		final ArrayList<Long> res = new ArrayList<Long>();
////		String q1 = "SELECT d.tstamp AS tstamp " + 
////					"FROM TaskDone d, TaskToDo t " + 
////					"WHERE d.task_id = t.task_id AND t.gen_id = ?";	
////		jt.query(q1, new Object[] { genId },
////		new RowCallbackHandler() {
////			public void processRow(ResultSet rs) throws SQLException {
////				res.add(rs.getLong("tstamp"));
////			}});
////		
////		return res;
//	}

	
	//************************ end of Behavior Performance functions*********************************
	
	
	//************************ Read/Writing Thumbnails from DB*******************************************
	/**
	 * Read the thumbnail as binary.
	 * 
	 * @param stimObjId
	 * @return thumbnail
	 */

	public byte[] readCanonicalThumbnail(int cat){
		return readBytes("CanonicalThumbnail","data",sArray(cat));
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		return jt.queryForObject(
//				"SELECT data FROM CanonicalThumbnail WHERE id=?",
//				new ParameterizedRowMapper<byte[]>() {
//					public byte[] mapRow(ResultSet rs, int rowNum)
//							throws SQLException {
//						return rs.getBytes("data");
//					}},
//				new Object[]{cat});
	}
	
	public byte[] readThumbnail(long stimObjId) {
		return readBytes("Thumbnail","data",sArray(stimObjId));
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		return jt.queryForObject(
//				"SELECT data FROM Thumbnail WHERE id=?",
//				new ParameterizedRowMapper<byte[]>() {
//					public byte[] mapRow(ResultSet rs, int rowNum)
//							throws SQLException {
//						return rs.getBytes("data");
//					}},
//				new Object[]{stimObjId});
	}
	
	public byte[] readThumbnailFromFile(long stimObjId){
		byte[] out = null;
		if (stimObjId!=0){
			String pngFileName = findFullPngFileName(stimObjId);
//			pngFileName = pngFileName + ".png";
			System.out.println(pngFileName);
			File imageFile = new File(pngFileName);
			//		System.out.print(imageFile.exists() + "\n");
			BufferedImage img = null;
			try {
				img = ImageIO.read(imageFile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out = ((DataBufferByte)img.getRaster().getDataBuffer()).getData();
			bgr2rgb(out);
		}
		return out;
	}
	
	private static String findFullPngFileName(long stimObjId) {
		stimObjId=1L;
		String pathname = System.getProperty("user.dir") + "/images";
		return findFullPngFileName(stimObjId,pathname);
	}
	private static String findFullPngFileName(long stimObjId,String dir) {
		String fileName = "stim_" + stimObjId + ".png";
		File folder = new File(dir);
		File[] listOfFiles = folder.listFiles();
		
	    for (int i = 0; i < listOfFiles.length; i++) {
	    	File curFile = listOfFiles[i];
	    	String curName = curFile.getName();
	    	String curFullName = dir + "/" + curName;
	    	if (curFile.isFile()) {
	    		if (curName.equals(fileName)) {
	    			return curFullName;   
	    		}
	    	}
	    	else if (curFile.isDirectory()) {
	    		String subOut = findFullPngFileName(stimObjId,curFullName);
	    		if (subOut!=null) {
	    			return subOut;
	    		}
	    	}
	    }
		return null;
	}
	
	void bgr2rgb(byte[] target) {
		byte tmp;

		for(int i=0x00000000; i<target.length; i+=0x00000003) {
			tmp = target[i];
			target[i] = target[i+0x00000002];
			target[i+0x00000002] = tmp;
		}
	}
	
	
	
	//******************** legacy stuff, maybe not currently used....***************
	
	//BehMsgEntry Batch ==> xper.classic.TrialExpMessDispatcher...
	/**
	 * Only the first "size" elements of the array are valid, and therefore saved to database.
	 * 
	 * @param msgs
	 * @param size
	 */
	public void writeBehMsgBatch(final BehMsgEntry[] msgs, final int size) {
		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.batchUpdate("insert into BehMsg (tstamp, type, msg) values (?, ?, ?)", 
		jt.batchUpdate("replace into BehMsg (tstamp, type, msg) values (?, ?, ?)", // TODO: change back to insert?
				new BatchPreparedStatementSetter() {
					public int getBatchSize() {
						return size;
					}

					public void setValues(PreparedStatement ps, int i)
							throws SQLException {
						ps.setLong(1, msgs[i].getTstamp());
						ps.setString(2, msgs[i].getType());
						ps.setString(3, msgs[i].getMsg());
					}
				});
	}

	
	// SimpleHistogram
	/**
	 * Get the StimId for a Task in the TaskToDo list.
	 * @param paramLong
	 * @return
	 */
	public long getStimIdByTaskId(long paramLong) {
		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
		Map<String, Object> localMap = jt.queryForMap(" select t.stim_id as id from TaskToDo t where t.task_id = ?", new Object[] { new Long(paramLong) });
		
//		JdbcTemplate localJdbcTemplate = new JdbcTemplate(this.dataSource);
//		Map<String, Object> localMap = localJdbcTemplate.queryForMap(" select t.stim_id as id from TaskToDo t where t.task_id = ?", new Object[] { new Long(paramLong) });
		//StimSpecEntry localStimSpecEntry = new StimSpecEntry();
		long l = ((Long)localMap.get("id")).longValue();
		return l;
	}
	
	// these were added by SHS
	
	
//	public void writeStimObjData(long id, String spec, String data) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("insert into StimObjData (id, spec, data) values (?, ?, ?)", 
//				new Object[] { id, spec, data });
//	}
	
//	public void updateStimObjData(long id, String data) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("update StimObjData set data = ? where id = ?", 
//				new Object[] { data, id });
//	}
	
//	public void updateInternalState(String name, int arr_ind, String val) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("update InternalState set val = ? where name = ? and arr_ind = ?", 
//						new Object[] { val, name, arr_ind });
//	}
//	public void writeInternalState(String name, int arr_ind, String val) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("insert into InternalState (name, arr_ind, val) values (?, ?, ?)", 
//						new Object[] { name, arr_ind, val });
//	}

	
	
	/**
	 * Read particular StimSpec.
	 * 
	 * @param stimId
	 * @return {@link StimSpecEntry}
	 */
	public StimSpecEntry readSingleStimSpec(long stimObjId) {
//		return new StimSpecEntry( toArray(readColsString("StimObjData", sArray(stimObjId))) ,"spec");
		return new StimSpecEntry( toArray(readColsString("StimObjData",new String[] {"id","spec","data"},sArray(stimObjId))) ,"spec");

//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		return jt.queryForObject(
//				" select id, spec from StimObjData where id = ? ", 
//				new ParameterizedRowMapper<StimSpecEntry> () {
//					public StimSpecEntry mapRow(ResultSet rs, int rowNum)
//							throws SQLException {
//						StimSpecEntry ent = new StimSpecEntry();
//
//						ent.setStimId(rs.getLong("id")); 
//						ent.setSpec(rs.getString("spec")); 
//
//						return ent;
//					}},
//				stimObjId);
	}

	public StimSpecEntry readSingleStimData(long stimObjId) {
//		return new StimSpecEntry( toArray(readColsString("StimObjData",sArray(stimObjId))) ,"data");		
		return new StimSpecEntry( toArray(readColsString("StimObjData",new String[] {"id","spec","data"},sArray(stimObjId))) ,"data");		
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		return jt.queryForObject(
//				" select id, data from StimObjData where id = ? ", 
//				new ParameterizedRowMapper<StimSpecEntry> () {
//					public StimSpecEntry mapRow(ResultSet rs, int rowNum)
//							throws SQLException {
//						StimSpecEntry ent = new StimSpecEntry();
//
//						ent.setStimId(rs.getLong("id")); 
//						ent.setSpec(rs.getString("data")); 
//
//						return ent;
//					}},
//				stimObjId);
	}
	
//	public String readSingleStimDataAsString(long stimObjId) {
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT data FROM StimObjData WHERE id = ?";
//		return jt.queryForObject(q, String.class, stimObjId);
//	}
	
//	public SachStimDataEntry readSingleStimData_v2(long stimObjId) {
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT data FROM StimObjData WHERE id = ?";
//		String s = jt.queryForObject(q, String.class, stimObjId);
//		return SachStimDataEntry.fromXml(s);
//	}
	
	
	// JK added this utility function to extract category for StimSpec XML
//	public String  getCategory(long stimObjId) {
//		return readString("StimObjData","ExtractValue(StimObjData.spec,'/StimSpec/category')",sArray(stimObjId));
//		
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		
////		String q = "SELECT ExtractValue(StimObjData.spec,'/StimSpec/category') FROM StimObjData  WHERE StimObjData.id = ?";
////		String cat = jt.queryForObject(q, String.class, stimObjId);
////		return cat;
//	}
	
	// JK 10 Nov 2016
	//  extract the random seed (if it exists) for a particular generation id
	//  This is needed in SachRandomGeneration::recreateGeneration(prevGenId)
//	public long getRandomSeed(long genId){ 
//		return readLong("SystemVar","val",new String[]{"name","'xper_random_seed'" , "tstamp,>,(SELECT MAX(TaskToDo.task_id) FROM TaskToDo WHERE TaskToDo.gen_id = ?)" , "limit"});
////		long id = -1;
////		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
////		
//////		String q = "SELECT val FROM SystemVar WHERE SystemVar.name = 'xper_random_seed' AND  SystemVar.tstamp >= (SELECT MIN(TaskToDo.task_id) FROM TaskToDo WHERE TaskToDo.gen_id = ?) LIMIT 1";
////		String q = "SELECT val FROM SystemVar WHERE SystemVar.name = 'xper_random_seed' AND  SystemVar.tstamp > (SELECT MAX(TaskToDo.task_id) FROM TaskToDo WHERE TaskToDo.gen_id = ?) LIMIT 1";		
////		String result = jt.queryForObject(q, String.class, genId - 1);
////		System.out.println("getRandomSeed () :" + result + ".");
////		
////		id = Long.parseLong(result);
////		 
////		
////		return id;
//		
//		
//	}
	
//	public List<SachStimDataEntry> readListStimData(long genId) {
//		final ArrayList<SachStimDataEntry> result = new ArrayList<SachStimDataEntry>();
//		
//		// TODO:
//		
//		// data = SachStimDataEntry.fromXml(dbUtil.readSingleStimData(stimObjId).getSpec());
//		
//		//SachExptSpec trialSpec = SachExptSpec.fromXml(dbUtil.getSpecByTaskId(taskId).getSpec());
//		
//		// get stimSpecs:
//		TreeMap<Long, StimSpecEntry> m = (TreeMap<Long, StimSpecEntry>) readStimSpecByGeneration(genId);
//		// get stimObjIds from stimspecs:
//		
//		
//		
//		return result;
//	}
	
	public List<SachStimDataEntry> readListStimData(List<Long> stimObjIds) {
		final ArrayList<SachStimDataEntry> result = new ArrayList<SachStimDataEntry>();
		for (long id : stimObjIds) {
			result.add(SachStimDataEntry.fromXml(readSingleStimData(id).getSpec()));
		}
		return result;
	}
	
	public List<BsplineObjectSpec> readListStimSpecs(List<Long> stimObjIds) {
		final ArrayList<BsplineObjectSpec> result = new ArrayList<BsplineObjectSpec>();
		for (long id : stimObjIds) {
			result.add(BsplineObjectSpec.fromXml(readSingleStimSpec(id).getSpec()));
		}
		return result;
	}
	
//	/**
//	 * My version of read all StimSpec for a generation.
//	 *
//	 * @param genId
//	 * @return list of StimSpecEntry
//	 */
//	public List<StimSpecEntry> readStimSpecsByGenId(long genId) {
//		ArrayList<StimSpecEntry> result;
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT s.spec AS spec FROM StimSpec s, TaskToDo d WHERE d.stim_id = s.id AND d.gen_id = ?";
//
//		result = (ArrayList<StimSpecEntry>) jt.query(q,
//				new ParameterizedRowMapper<StimSpecEntry>() {
//					public StimSpecEntry mapRow(ResultSet rs, int rowNum)
//							throws SQLException {
//						StimSpecEntry ent = new StimSpecEntry();
//
//						ent.setStimId(rs.getLong("id")); 
//						ent.setSpec(rs.getString("spec")); 
//
//						return ent;
//					}},
//				genId);
//		
//		return result;
//	}
	
	
//	public long readBehMsgTrialStart(long tstamp) {
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT tstamp FROM BehMsg WHERE type='TrialStart' AND tstamp < ? ORDER BY tstamp DESC LIMIT 1";
//		return jt.queryForLong(q,tstamp);
//	}
//
//	// read TrialOutcome msg given TrialStart tstamp
//	public String readTrialOutcomeByTrialStartTime(long tstamp) {
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT msg FROM BehMsg WHERE type='TrialOutcome' AND tstamp > ? ORDER BY tstamp LIMIT 1";
//		return jt.queryForObject(q, String.class, tstamp);
//	}
//	
//	public String readTrialOutcomeByTaskDoneTime(long tstamp) {
//		return readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
//	}
//
//	// in order to check that a certain TrialOutcome belongs to a certain taskId, we need to verify this
//	public String readTrialOutcomeByTaskId(long taskId) {
//		long tstamp = readTaskDoneTimeLast(taskId);
//		String trialOutcomeString = readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
//		SachTrialOutcomeMessage msg = SachTrialOutcomeMessage.fromXml(trialOutcomeString);
//		String trialOutcome = msg.getOutcome();
//		
//		// checking if trialOutcome matches taskId
//		long taskIdCheck = msg.getTaskID();
//		if (taskIdCheck != taskId) {
//			trialOutcome = "N/A";
//		} 
//				
//		return trialOutcome;
//	}
//	
//	public SachTrialOutcomeMessage readTrialOutcomeMsgByTaskId(long taskId) {
//		long tstamp = readTaskDoneTimeLast(taskId);
//		String trialOutcomeString = readTrialOutcomeByTrialStartTime(readBehMsgTrialStart(tstamp));
//		SachTrialOutcomeMessage msg = SachTrialOutcomeMessage.fromXml(trialOutcomeString);
//		String trialOutcome = msg.getOutcome();
//		
//		// checking if trialOutcome matches taskId
//		long taskIdCheck = msg.getTaskID();
//		if (taskIdCheck != taskId) {
//			trialOutcome = "N/A";
//			msg.setOutcome(trialOutcome);
//		} 
//		
//		return msg;
//	}
	
	
	
	// ExpLog table -- my versions allow other variables to be written, like 'status','trialType','globalGenId','localGenId','isRealExp',etc
		// write:
	/**
	 * Write experiment log message.
	 * 
	 * @param tstamp
	 * @param log
	 */

//	public void writeExpLog(long tstamp, String log) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("insert into ExpLog(tstamp, memo) values (?, ?)", 
//				new Object[] {tstamp, log });
//	}
	
//	public void writeExpLog(long tstamp, String exptType, String status, long globalGenId, long localGenId, boolean isRealExpt, String log) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.update("insert into ExpLog(tstamp,type,status,globalGenId,localGenId,isRealExpt,memo) values (?,?,?,?,?,?,?)", 
//				new Object[] {tstamp,exptType,status,globalGenId,localGenId,isRealExpt,log});
//	}
	
//	public void writeExpLog(SachExpLogMessage msg) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String query = "insert into ExpLog(tstamp,dateTime,type,status,depth,globalGenId,localGenId,firstGlobalGenId,cellNum,isRealExpt,memo) values (?,?,?,?,?,?,?,?,?,?,?)";
//		jt.update(query, new Object[] {msg.getTimestamp(),msg.getDateTime(),msg.getTrialType(),msg.getStatus(),
//							msg.getDepth(),msg.getGlobalGenId(),msg.getGenNum(),msg.getFirstGenNum(),msg.getCellNum(),msg.getRealExp(),SachExpLogMessage.toXml(msg)});
//	}
	
	
//	public void writeExpLog(SachExpLogMessage msg, String log) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String query = "insert into ExpLog(tstamp,dateTime,type,status,depth,globalGenId,localGenId,cellNum,isRealExpt,memo) values (?,?,?,?,?,?,?,?,?,?)";
//		jt.update(query, new Object[] {msg.getTimestamp(),msg.getDateTime(),msg.getTrialType(),msg.getStatus(),
//							msg.getDepth(),msg.getGlobalGenId(),msg.getGenNum(),msg.getCellNum(),msg.getRealExp(),log});
//	}
//	
	
		// read:
	/**
	 * Read ExpLog with time stamp between startTime and stopTime.
	 * 
	 * @param startTime
	 * @param stopTime
	 * @return List of {@link ExpLogEntry}
	 */
//	public List<ExpLogEntry> readExpLog(long startTime, long stopTime) {
//		final ArrayList<ExpLogEntry> result = new ArrayList<ExpLogEntry>();
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		jt.query(
//			" select tstamp, memo " + 
//			" from ExpLog " + 
//			" where tstamp >= ? and tstamp <= ?" + 
//			" order by tstamp ", 
//			new Object[] {startTime, stopTime },
//			new RowCallbackHandler() {
//				public void processRow(ResultSet rs) throws SQLException {
//					ExpLogEntry ent = new ExpLogEntry();
//					ent.setTstamp(rs.getLong("tstamp")); 
//					ent.setLog(rs.getString("memo")); 
//
//					result.add(ent);
//				}});
//		return result;
//	}

//	public SachExpLogMessage readLastExpLog() {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//
//		final ArrayList<SachExpLogMessage> result = new ArrayList<SachExpLogMessage>();
//		jt.query("SELECT @maxGenId:=max(globalGenId) FROM ExpLog ;" +
//				"SELECT tstamp,type,status,depth,globalGenId,localGenId,firstGlobalGenId,cellNum,isRealExpt  " + 
//				"WHERE globalGenId = @maxGenId;", 
//				new RowCallbackHandler() {
//					public void processRow(ResultSet rs) throws SQLException {
//						SachExpLogMessage msg = new SachExpLogMessage(	rs.getString("status"),			rs.getString("trialType"),		rs.getLong("depth"),
//								                                      															rs.getLong("localGenId"),	rs.getLong("globalGenId"),	rs.getLong("firstGlobalGenId"),
//								                                      															rs.getLong("cellNum"), 		rs.getBoolean("isRealExpt"), rs.getLong("tstamp"));
//						
//						result.add(msg);
//					}});
//		
//		return result.get(0);
//		
//	}
//	public String readLastExpLogStatus(long genId) {
//		SimpleJdbcTemplate jt = new SimpleJdbcTemplate(dataSource);
//		String q = "SELECT status FROM ExpLog WHERE globalGenId=? order by tstamp desc limit 1";
//		return jt.queryForObject(q, String.class,genId);
//	}
	
//	public List<Long> readGAGenIdFromExpLog(){
//		final ArrayList<Long> result = new ArrayList<Long>();
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String q = "select globalGenId from ExpLog where (status='GEN_DONE' or status='START') and type='GA' order by tstamp desc limit 100";
//		jt.query(q,
//				new Object[] {  },
//				new RowCallbackHandler() {
//					public void processRow(ResultSet rs) throws SQLException {
//						Long val = rs.getLong("globalGenId");
////						if(!result.contains(val)) {
//							result.add(val);
////						}
//					}});
//		return result;
//	}
	
//	public List<Long> readGAGenSetFromExpLog(){
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		long firstGenId = jt.queryForLong("select firstGlobalGenId from ExpLog where isRealExpt=1 and status='GEN_DONE' and type='GA' order by tstamp desc limit 1");
//		final ArrayList<Long> result = new ArrayList<Long>();
//		String q = "select globalGenId from ExpLog where status='GEN_DONE' and type='GA' and firstGlobalGenId=? order by tstamp desc";
//		jt.query(q,
//				new Object[] { firstGenId },
//				new RowCallbackHandler() {
//					public void processRow(ResultSet rs) throws SQLException {
//						Long val = rs.getLong("globalGenId");
////						if(!result.contains(val)) {
//							result.add(val);
////						}
//					}});
//		return result;
//	}
	public List<Long> readGAGenSetFromExpLog(long genId){
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
		long firstGenId;
		String type;
		if (genId==-1){ 	
			firstGenId 	= readLong(		"ExpLog","firstGlobalGenId",new String[] {"isRealExpt,1" , "status,'GEN_DONE'" , "type,'GA'" , "odl"});
			type 		= readString(	"ExpLog","type",			new String[] {"isRealExpt,1" , "status,'GEN_DONE'" , "type,'GA'" , "odl"});
		}
		else {
			firstGenId 	= readLong(		"ExpLog","firstGlobalGenId",new String[] {"globalGenId," + genId , "limit"});
			type 		= readString( 	"ExpLog","type",			new String[] {"globalGenId," + genId , "limit"});
		}
//		type = addQ(type);
		if (firstGenId==-1)	return new ArrayList<Long>();
		else 				return readRowsLong("ExpLog","globalGenId",new String[]{"status,'GEN_DONE'" , "type," + addQ(type) , "firstGlobalGenId," + firstGenId , "order,desc"});
		
//		long firstGenId = 	jt.queryForLong("select firstGlobalGenId from ExpLog where globalGenId=" + genId + " limit 1");
//		String type = (String) jt.queryForObject("select type from ExpLog where globalGenId=" + genId + " limit 1", String.class);
//		
//		final ArrayList<Long> result = new ArrayList<Long>();
//		String q = "select globalGenId from ExpLog where status='GEN_DONE' and type=? and firstGlobalGenId=? order by tstamp desc";
//		jt.query(q,
//				new Object[] { type, firstGenId},
//				new RowCallbackHandler() {
//					public void processRow(ResultSet rs) throws SQLException {
//						Long val = rs.getLong("globalGenId");
////						if(!result.contains(val)) {
//							result.add(val);
////						}
//					}});
//		return result;
	}
	

	public void writeStimObjIdForEachGenId(long genId, long stimObjId) {
		writeLine("GenId_to_StimObjId",new Object[] {genId, stimObjId});
//		List<Long> stimObjIds = new ArrayList<Long>();
//		stimObjIds.add(stimObjId);
//		writeStimObjIdsForEachGenId(genId,stimObjIds);
	}
	public void writeStimObjIdsForEachGenId(long genId, List<Long> stimObjIds) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		String q = "INSERT INTO GenId_to_StimObjId (gen_id, stimObjId) VALUES (?, ?)";
		for (long id : stimObjIds) {
			writeStimObjIdForEachGenId(genId,id);
//			jt.update(q, new Object[] { genId, id });
		}
	}
	
	public List<Long> readNonBlankStimObjIdsFromGenId(long genId) {
		List<Long> allStimObjIds = readStimObjIdsFromGenId(genId);
		List<Long> res = new ArrayList<Long>();
		for (int ss=0;ss<allStimObjIds.size();ss++){
			BsplineObjectSpec spec = BsplineObjectSpec.fromXml(readSingleStimSpec(allStimObjIds.get(ss)).getSpec());
			
			if (!spec.isBlankStim()) res.add(allStimObjIds.get(ss));
		}
		return res;
		
	}
	
	public List<Long> readStimObjIdsFromGenId(long genId) {
		return readRowsLong("GenId_to_StimObjId","stimObjId",sArray(genId));
		
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		final ArrayList<Long> res = new ArrayList<Long>();
//		String q = "SELECT stimObjId FROM GenId_to_StimObjId WHERE gen_id = ?";	
//		jt.query(q, new Object[] { genId },
//		new RowCallbackHandler() {
//			public void processRow(ResultSet rs) throws SQLException {
//				res.add(rs.getLong("stimObjId"));
//			}});
//		
//		return res;
	}
	
//	public List<Long> readSubsetStimObjIds(long start,long end,String type) {
//		JdbcTemplate jt = new JdbcTemplate(dataSource);
//		final ArrayList<Long> res = new ArrayList<Long>();
//		String q = "SELECT id FROM StimObjData WHERE id>? and id<? and data like '%" + type + "%'";	
//		jt.query(q, new Object[] { start,end },
//		new RowCallbackHandler() {
//			public void processRow(ResultSet rs) throws SQLException {
//				res.add(rs.getLong("id"));
//			}});
//		
//		return res;
//	}
	
	
	// JK 1/7/2015 
	// 
	public List<Long> getGenIdsForBEH_train() {
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		final ArrayList<Long> res = new ArrayList<Long>();
		String q = "SELECT DISTINCT globalGenId from BEH_train_GenIds";
//		String q = "SELECT T2.tstamp, T2.task_id, T2.part_done, T3.stim_id, T3.globalGenId " +
//						"FROM TaskDone T2 " +
//						"inner join " + 
//						" ( SELECT T.task_id, T.stim_id, E.globalGenId " + 
//						" FROM TaskToDo T " +
//						" inner join ( " +
//						" SELECT globalGenId " + 
//						" FROM ExpLog " +
//						" where " +
//						" isRealExpt = 1 " +
//						" and type = 'BEH_train' " +
//						" and status = 'START') E " +
//						" ON T.gen_id = E.globalGenId) T3" +
//						" ON  T2.task_id = T3.task_id" ;
		jt.query(q, new Object[] {  },
		new RowCallbackHandler() {
			public void processRow(ResultSet rs) throws SQLException {
				Long val = rs.getLong("globalGenId");
				if(!res.contains(val)) {
					res.add(val);
				}
			}});
		
		return res;
		
	}
	
	
	public Map<Integer, OccluderManifoldSpec> getOccludedSeriesSpecMap() {
		final Map<Integer, OccluderManifoldSpec> occluderSpecMap = new HashMap<Integer, OccluderManifoldSpec>();
		
		JdbcTemplate jt = new JdbcTemplate(dataSource);
		String query = "SELECT * from OccludedSeries";

		jt.query(query, new Object[] {  },
		new RowCallbackHandler() {
			
			public void processRow(ResultSet rs) throws SQLException {
				int seriesId = 0;
				int order = 0;
				OccluderManifoldSpec oms = new OccluderManifoldSpec();
				ArrayList<Double> locs = new ArrayList<Double>();
				ArrayList<Double> offsets = new ArrayList<Double>();
				ArrayList<Integer> morphLimbs = new ArrayList<Integer>();
				
				seriesId = rs.getInt("SeriesId");
				order = rs.getInt("Order");
				oms.setSeriesId(seriesId);
				oms.setOrder(order);
				oms.setCategory(rs.getInt("Category"));
				// push the 4 location values IN ORDER!  Location 4 is 50/50 ambiguity so always rewarded
				locs.add(0, Double.valueOf(rs.getDouble("Loc1")));
				locs.add(1, Double.valueOf(rs.getDouble("Loc2")));
				locs.add(2, Double.valueOf(rs.getDouble("Loc3")));
				locs.add(3, Double.valueOf(rs.getDouble("Loc4")));
				oms.setLocations(locs);
				// JK let setLocations() filter out dummy entries and set the numLocations 
				//    oms.setNumLocations(locs.size());
				offsets.add(0, Double.valueOf(rs.getDouble("Offset1")));
				offsets.add(1, Double.valueOf(rs.getDouble("Offset2")));
				offsets.add(2, Double.valueOf(rs.getDouble("Offset3")));
				offsets.add(3, Double.valueOf(rs.getDouble("Offset4")));
				oms.setOffsets(offsets);
				
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb1")));
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb2")));
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb3")));
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb4")));
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb5")));
				morphLimbs.add(0, Integer.valueOf(rs.getInt("MorphLimb6")));
				oms.setMorphLimbs(morphLimbs);
				
				oms.setMorphMax(rs.getDouble("MorphMax"));
				oms.setMorphMin(rs.getDouble("MorphMin"));
				oms.setNode1(rs.getInt("Node1"));
				oms.setNode2(rs.getInt("Node2"));
				oms.setPerpDist(rs.getDouble("PerpDist"));
//				oms.setParaDist1(rs.getDouble("ParaDist1"));
//				oms.setParaDist2(rs.getDouble("ParaDist2"));
				oms.setInnerRadius(rs.getDouble("InnerRadius"));
				oms.setOuterRadius(rs.getDouble("OuterRadius"));
				oms.setCenterX(rs.getDouble("Center_x"));
				oms.setCenterY(rs.getDouble("Center_y"));
				oms.setId(rs.getInt("id"));
				occluderSpecMap.put((seriesId - 1) * 2 + (order - 1), oms);
				
//				oms = occluderSpecMap.get(rs.getInt("SeriesId"));
//				System.out.format("oms  : %4d, %4d, %4d \n", oms.getSeriesId(), oms.getCategory(), oms.getNode1());
//				System.out.format("oms  : %4d, %4d, %4d \n", rs.getInt("SeriesId"), rs.getInt("Stim1"), rs.getInt("Stim2"));

				
			}});
		
		
		
		return occluderSpecMap;
		
		
	}
//	// for selecting the dbUtil data source: -shs
//	public void createDbUtil() {
//		// -- for testing only
//		CreateDbDataSource dataSourceMaker = new CreateDbDataSource();
////		setDbUtil(new DbUtil(dataSourceMaker.getDataSource()));
//		setDataSource(dataSourceMaker.getDataSource());
//	}
	
}
