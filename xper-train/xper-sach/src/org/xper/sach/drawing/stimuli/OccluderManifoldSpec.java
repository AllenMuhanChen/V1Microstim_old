package org.xper.sach.drawing.stimuli;

import java.util.ArrayList;
import java.util.List;

import org.xper.sach.util.SachMathUtil;

// container for Occluder specification parameters 
//     seriesId 
public class OccluderManifoldSpec {
	final static Double DummyValue = new Double(999.0);
	Integer seriesId = new Integer(-1);
	int category = -1;
	int order = -1;
	int numLocations = -1;
	int node1 = -1;
	int node2 = -1;
	int id = -1;
	
	double alphaGain = 0.2;
	
	double perpDist =  0.0;
//	double paraDist1 = 0.0;
//	double paraDist2 = 0.0;
	double innerRad = 0.0;
	double outerRad = 0.0;
	double centerX = 0.0;
	double centerY = 0.0;
	double morphMax = 0.0;
	double morphMin = 0.0;
	
	ArrayList<Double> locations = new ArrayList<Double>();
	ArrayList<Double> offsets = new ArrayList<Double>();
	ArrayList<Integer> morphLimbs = new ArrayList<Integer>();


	public void setLocations(ArrayList<Double> locs){
		// locs may contain dummy place holder 999
		int ndx = 0; 
		while(ndx != -1){
			ndx = locs.indexOf(DummyValue);
			if(ndx != -1) locs.remove(ndx);
		}
		locations = locs;
		numLocations = locations.size();
		
//		System.out.println("--- OccluderManifoldSpec::setLocations() ---");
//		for(int i = 0; i < locations.size(); i++){
//			System.out.format("%4.2f  " , locations.get(i));
//		}
	
	}
	
	
	public void setOffsets(ArrayList<Double> offsets){
		// offsets may contain dummy place holder 999
		int ndx = 0; 
		while(ndx != -1){
			ndx = offsets.indexOf(DummyValue);
			if(ndx != -1) offsets.remove(ndx);
		}
		this.offsets = offsets;
	
		
//		System.out.println("--- OccluderManifoldSpec::setOffsets() ---");
//		for(int i = 0; i < locations.size(); i++){
//			System.out.format("%4.2f  " , locations.get(i));
//		}
	
	}
	
	
	public void setMorphLimbs(ArrayList<Integer> limbsToMorph){
		// morphLimbs may contain dummy place holder 999
		int ndx = 0; 
		while(ndx != -1){
			ndx = limbsToMorph.indexOf(DummyValue.intValue());
			if(ndx != -1) limbsToMorph.remove(ndx);
		}
		this.morphLimbs = limbsToMorph;
	}
	
	public List<Integer> getSomeLimbsToMorph(){
		// randomly select how many limbs to morph between 1 and numLimbs
		int numLimbs = SachMathUtil.randRange(morphLimbs.size(), 1);
		List<Integer> indices = SachMathUtil.randUniqueRange(morphLimbs.size() - 1, 0, numLimbs);
		List<Integer> morphList = new ArrayList<Integer>();
		
		
//		System.out.print("OccluderManifoldSpec::getSomeLimbsToMorph()  morphing " + numLimbs + " limbs : ");
		for(int i = 0; i < numLimbs; i++){
			morphList.add(morphLimbs.get(indices.get(i)));
//			System.out.format("%d  " , morphList.get(i));
		}
//		System.out.println("");
		
		return morphList;
	}
	
	public void setSeriesId(int id){
		seriesId = id;
	}
	
	public Integer getSeriesId(){
		
		return new Integer(seriesId);
	}
	
	
	
	public void setMorphMax(double morphMax){
		this.morphMax = morphMax;
	}
	
	
	public double getMorphMax(){
		return morphMax;
	}
	
	public double getMorphMin(){
		return morphMin;
	}
	

	
	public void setMorphMin(double morphMin){
		this.morphMin = morphMin;
	}
	
	
	public void setId(int id){
		this.id = id;
	}
	
	public Integer getId(){
		return Integer.valueOf(id);
	}
	
	public void setCategory(int category){
		this.category = category;
	}
	
	
	public int getCategory(){
		return category;
	}
	
	public double getCenterX(){
		return centerX;
	}
	
	public void setCenterX(double centerX){
		this.centerX = centerX;
	}
	
	public double getCenterY(){
		return centerY;
	}
	
	public void setCenterY(double centerY){
		this.centerY = centerY;
	}
	
	
	public void setNumLocations(int numLocations){
		this.numLocations = numLocations;
	}
	
	public int getNumLocations(){
		return numLocations;
	}
	
	public double getLocation(int whichLocation){
		return locations.get(whichLocation);
	}
	
	public double getOffsetVal(int whichOffset){
		return offsets.get(whichOffset);
	}
	
	
	public void setNode1(int node1){
		this.node1 = node1;
	}
	
	public int getNode1(){
		return node1;
	}

	public void setNode2(int node2){
		this.node2 = node2;
	}
	
	public int getNode2(){
		return node2;
	}
	
	public void setPerpDist(double perpDist){
		this.perpDist = perpDist;
	}	
	
	public double getPerpDist(){
		return perpDist;
	}

	
//	public void setParaDist1(double paraDist1){
//		this.paraDist1 = paraDist1;
//	}	
//
//	public double getParaDist1(){
//		return paraDist1;
//	}
//
//
//	public void setParaDist2(double paraDist2){
//		this.paraDist2 = paraDist2;
//	}	
//	
//	public double getParaDist2(){
//		return paraDist2;
//	}
	
	
	public void setInnerRadius(double innerRadius){
		this.innerRad = innerRadius;
	}
	
	public double getInnerRadius(){
		return innerRad;
	}

	public void setOuterRadius(double outerRad){
		this.outerRad = outerRad;
	}
	
	
	public double getOuterRadius(){
		return outerRad;
	}
	
	
	public void setOrder(int order){
		this.order = order;
	}
	
	public int getOrder(){
		return order;
	}
	
	public double getAlphaGain(){
		return alphaGain;
	}
	
	public void setAlphaGain(double alphaGain){
		this.alphaGain = alphaGain;
	}
	
	

}
