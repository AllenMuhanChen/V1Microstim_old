package org.xper.sach.drawing.stimuli;

import com.thoughtworks.xstream.XStream;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.BufferUtils;
import org.xper.sach.drawing.splines.MyPoint;
import org.xper.sach.vo.SachTrialOutcomeMessage;

/** 
OccluderSpec - stores parameters to create the occluder.  Currently a rectangle with 1 circular aperture. The rectangle 
 center, width, and height can be specified. Each hole is specified by x and y center location, an inner radius and an outer radius.  
 The radii are used in the GLSL shader program to smoothly tranisition from alpha = 1 to alpha = 0.
 
 The locationVal and offsetVal specify a "position" along the manifold
 
**/

public class OccluderSpec {
	
	// rectangle parameters
	float xCenter = 0.0f;
	float yCenter = 20.0f;
	float height = 30.0f;
	float width = 60.0f;
	float alphaGain = 1.0f; 
	int numHoles = 1;
		
	// save the seriesId, offset value and location value used to create this occluder
	float offsetVal = 0.0f;
	float locationVal = 0.0f;
	int seriesId = -1;
		
	// use a FloatBuffer to store the hole specifications passed into the shader
	//FloatBuffer fb;
	float[] apertureSpecs;
	
	static final int NumSpecsPerHole = 4;	// xCenter1, yCenter1, innerRadius1, outerRadius1, ... 
	static final int MaxNumberHoles = 1;    // 
	
	transient static XStream s;
	
	static {
		s = new XStream();
		s.alias("Occluder", OccluderSpec.class);
	}
	
	public static  void main(String[] args) {
		float fb[] = {91.0f, 92.0f, 93.0f, 94.0f};
		
		OccluderSpec spec = new OccluderSpec();
		spec.setXCenter(220.0f);
		spec.setYCenter(60.0f);
		spec.setHeight(1060);
		spec.setWidth(1340);
		spec.setAlphaGain(40.6f);
		spec.setSpecs(fb);
		spec.setSeriesId(999);
		spec.setLocationVal(0.9999f);
		spec.setOffsetVal(0.0009f);
		
		
		//System.out.println(spec.toXml());
		
		String xmlstr = "<Occluder>	  <xCenter>0.0</xCenter>  <yCenter>0.0</yCenter> " +
				" <height>100.0</height>  <width>130.0</width>	  <alphaGain>0.6</alphaGain>" +
				"  <numHoles>1</numHoles>  <offsetVal>0.12345</offsetVal>	  <locationVal>0.12345</locationVal>" +
				"	  <seriesId>999</seriesId>	  <apertureSpecs>	    <float>1.0</float>	    <float>2.0</float>	" +
				"    <float>3.0</float>	    <float>46.0</float>	  </apertureSpecs>	</Occluder>";
		spec = new OccluderSpec();
		
		spec = OccluderSpec.fromXml(xmlstr);
		
		System.out.println(spec.toXml());
		
		
		
	}
	
	
	public void setSpecs(float[] specList){
		
		// float array test.  LWJGL recommends using the BufferUtils.
		// specs will contain:
		//      xCenter1, yCenter1, innerRadius1, outerRadius1, 
		//      xCenter2, yCenter2, innerRadius2, outerRadius2,  ... 
		apertureSpecs = specList; // BufferUtils.createFloatBuffer(numHoles * NumSpecsPerHole );
		numHoles = apertureSpecs.length / NumSpecsPerHole;

		
	}
	
	public FloatBuffer getSpecs(){

		FloatBuffer fb = BufferUtils.createFloatBuffer(numHoles * NumSpecsPerHole );
		fb.put(apertureSpecs);
		fb.rewind();
		return fb;
		
	}
	
	
	public void setCenter(float x, float y) {
		xCenter = x;
		yCenter = y;
	}

	public void setWidth(int widthInPixels) {
		width = widthInPixels;
	}
	
	public float getWidth() {
		return width;
	}

	public void setHeight(int heightInPixels) {
		height = heightInPixels;
	}
	
	public float getHeight() {
		return height;
	}
	
	public void setLocationVal(float locationVal) {
		this.locationVal = locationVal;
	}
	
	public float getLocationVal() {
		return locationVal;
	}
		
	
	public void setAlphaGain(float a) {
		alphaGain = a;		
	}
	
	public float getAlphaGain() {
		return alphaGain;
	}

	public String toXml () {
		return OccluderSpec.toXml(this);
	}
	
	public static String toXml (OccluderSpec spec) {
		return s.toXML(spec);
	}
	
	public static OccluderSpec fromXml (String xml) {
		OccluderSpec g = (OccluderSpec)s.fromXML(xml);
		return g;
	}
	
	public OccluderSpec() {	}
	
	public OccluderSpec(OccluderSpec d) {

		System.out.println("OccluderSpec copy ");
		// rectangle parameters
		xCenter = d.xCenter;
		yCenter = d.yCenter;
		height = d.height;
		width = d.width;
		alphaGain = d.alphaGain; 
		numHoles = d.numHoles;
		locationVal = d.locationVal;
		offsetVal = d.offsetVal;
		seriesId = d.seriesId;
		
		int len = d.apertureSpecs.length;
		System.arraycopy(d.apertureSpecs, 0, apertureSpecs, 0, len);

	}
	
	public float getXCenter() {
		return xCenter;
	}
	public void setXCenter(float center) {
		xCenter = center;
	}
	public float getYCenter() {
		return yCenter;
	}
	public void setYCenter(float center) {
		yCenter = center;
	}
//	public float getRadius() {
//		return radius;
//	}
//	public void setRadius(float radius) {
//		this.radius = radius;
//	}

	public int getNumHoles(){
		return numHoles;
	}
	
	public float getOffsetVal(){
		return offsetVal;
	}
	
	public void setOffsetVal(float val){
		offsetVal = val;
//		if(val > 1 || val < 0){
//			System.out.println("OccluderSpec setLocationVal() : bad value " + val);
//			locationVal = 0.0f;
//			
//		} else {
//			locationVal = val;
//		}
	}
	
	
	public int getSeriesId(){
		return seriesId;
		
	}
	
	public void setSeriesId(int val){
		seriesId = val;
	}

	public void foo(){
		System.out.println("OccluderSpec::foo()");
		
	}

	
	
}
