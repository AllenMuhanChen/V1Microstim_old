package org.xper.sach.expt;

import org.xper.sach.somlab.SachEstimSpec;



public class SachEstimExptSpec extends SachExptSpec {
	
	SachEstimSpec estimSpec;
//	SachExptSpec exptSpec;
	
	static {
		s.alias("SachEstimExptSpec", SachEstimExptSpec.class);
	}

	public SachEstimSpec getEstimSpec() {
		return estimSpec;
	}

	public void setEstimSpec(SachEstimSpec spec) {
		this.estimSpec = spec;
	}
	
	public static String toXml(SachEstimExptSpec spec) {
		return s.toXML(spec);
	}
	
	public static SachEstimExptSpec fromXml(String xml) {
		SachEstimExptSpec g = (SachEstimExptSpec)s.fromXML(xml);
		return g;
	}
	
//	public static void main(String[] args) {  
//		 //for testing
//		SachEstimSpec s = new SachEstimSpec(true, 0, new int[]{0,1}, new int[]{0,0}, true, new int[]{-1,-1}, new int[]{1,1}, new int[]{1,1}, new int[]{0,0}, new double[]{200,300}, 0, 100);
//		SachEstimExptSpec spec = new SachEstimExptSpec();
//		spec.setEstimSpec(s);
////		spec.addStimObjId(123456789);
////		System.out.println(s.toXml());
//		System.out.println(spec.toXml());
//	}
	
}
