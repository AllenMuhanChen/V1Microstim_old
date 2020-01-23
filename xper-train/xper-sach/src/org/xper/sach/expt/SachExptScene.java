package org.xper.sach.expt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.xper.Dependency;
import org.xper.classic.vo.TrialContext;
import org.xper.db.vo.StimSpecEntry;
import org.xper.db.vo.SystemVariable;
import org.xper.drawing.Context;
import org.xper.drawing.RGBColor;
import org.xper.experiment.ExperimentTask;
import org.xper.sach.AbstractSachTaskScene;
import org.xper.sach.drawing.screenobj.DiskObject;
import org.xper.sach.drawing.screenobj.RectangleObject;
import org.xper.sach.drawing.stimuli.BsplineObject;
import org.xper.sach.drawing.stimuli.BsplineObjectSpec;
//import org.xper.sach.renderer.SachPerspectiveStereoRenderer;
import org.xper.sach.util.SachDbUtil;
import org.xper.sach.util.SachExperimentUtil;
import org.xper.sach.vo.SachTrialContext;

public class SachExptScene extends AbstractSachTaskScene {
	
	@Dependency
	SachDbUtil dbUtil;
	
	// --- initialize response target
	DiskObject responseSpot = new DiskObject();		// to change specs, change in DiskObject
	boolean drawResponseSpot;						// this is the target position marker (for behavioral trials)
//	RectangleObject extraFixationSpot = new RectangleObject(0.80f,0.80f,0.78f,4,4);  // Background color is (0.5, 0.5, 0.5);
	RectangleObject extraFixationSpot = new RectangleObject(0.80f, 0.80f, 0.80f, 4, 4);  // Background color is (0.5, 0.5, 0.5);

	//BsplineObject spline = new BsplineObject();
	
	// r2_sach@ecpc48:~/Documents/take_trainingForSach/xper_sach7_take_trainingForSach$ scp -r dist m2_sach@172.30.6.46:/mnt/data/home/m2_sach/Documents/take_trainingForSach/
    // r2_sach@ecpc48:~/Documents/take_trainingForSach/xper_sach7_take_trainingForSach/dist$ scp -r . m2_sach@172.30.6.46:/mnt/data/home/m2_sach/Documents/take_trainingForSach/dist
    // r2_sach@ecpc48:~/Documents/take_trainingForSach/xper_sach7_take_trainingForSach/dist$ scp -r . m2_sach@172.30.6.46:/mnt/data/home/m2_sach/Documents/take_trainingForSach/dist

	
	List<BsplineObject> objects = new ArrayList<BsplineObject>();
	SachExptSpec spec = new SachExptSpec();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		
//		// set response spot size and location:
//		SachPerspectiveStereoRenderer rend = (SachPerspectiveStereoRenderer)this.renderer;
//		double d = rend.getDistance();
		
		
		// JK  3 Aug 2016
		// set diskObject responseSpot center
		// responseSpot.tx = 100;
		// 
		// Map<String, SystemVariable> sysVar = dbUtil.readSystemVar("xper_response_spot_location%");
		// responseSpot.tx = Double.parseDouble(sysVar.get("xper_response_spot_location").getValue(0));
		// responseSpot.ty = Double.parseDouble(sysVar.get("xper_response_spot_location").getValue(1));
		 
		
	}

	public void setTask(ExperimentTask task) {
		objects.clear();
		spec = SachExptSpec.fromXml(task.getStimSpec());
		for (int i = 0; i < spec.getStimObjIdCount(); i ++) {
			long id = spec.getStimObjId(i);
//			StimSpecEntry ent = dbUtil.readStimSpecFromStimObjId(id);
			StimSpecEntry ent = dbUtil.readSingleStimSpec(id);
			
			BsplineObjectSpec g = BsplineObjectSpec.fromXml(ent.getSpec()); 
			g.setDoMorph(false); // make sure it doesn't try to re-morph obj
			
			BsplineObject obj = new BsplineObject();
			obj.setCantFail(true); // at this point, do not allow any stims to fail
			obj.setPrintStimSpecs(false);	// do not print debugging info here, only via '*RandGen'
			obj.setSpec(g.toXml());
			objects.add(obj);
		}
//		dbUtil.resetSource();
		//System.out.println("setTask numObjs=" + objects.size());
	}

	public void drawStimulus(Context context) {
		TrialContext c = (TrialContext)context;
		if (drawResponseSpot) responseSpot.draw(c); 
		
		int index = c.getSlideIndex();
		int numObjs = objects.size();
		
		//System.out.println("drawStim slide=" + index);
		if ((index >= 0) && (index < numObjs)) {
			BsplineObject obj = objects.get(index);
			obj.draw(c);
		}
	}

	protected void drawTargetObjects(Context context) {
		SachTrialContext c = (SachTrialContext)context;

//	JK	responseSpot.setB(1.0f);
//	JK	responseSpot.setR(0.0f);
		
		if (drawResponseSpot) responseSpot.draw(c);		
		
//		JK		responseSpot.setB(0.8f);
//		JK		responseSpot.setR(0.8f);	
	
		// --- redraw the target, to show the object at the target position (for training):
		int index = c.getSlideIndex();
		long targetIndex = c.getTargetIndex();
		
// JK 		System.out.println("drawTargetObjs slide=" + index);

		if (index >= 0 && index < objects.size()) {
			if (targetIndex >= 0) {
				
//				//  Start
//				BsplineObject obj = objects.get(index);
//				
//				double xPos = c.getTargetPos().getX();	// get the target positions from the trial context
//				double yPos = c.getTargetPos().getY();
//				xPos = c.getRenderer().deg2mm(xPos);	// convert units
//				yPos = c.getRenderer().deg2mm(yPos);
//
//				obj.setxPos(xPos);		// shift the obj position to that of the target position
//				obj.setyPos(yPos);
//				obj.draw(c);			// show obj at target position
//				//  End
				
				extraFixationSpot.drawRectangle(c);
			}
			
		}

				// --- for showing a simple circle around target position:
		// JK July18_2016  uncommented cue:
//		SachExperimentUtil.drawFilledTargetEyeWindow(c.getRenderer(), c.getTargetPos(), c.getTargetEyeWindowSize()*.3, new RGBColor(1f, 1f, 0f)); 

	}
	
	protected void drawCustomBlank(Context context) {
		// draw your customized blank screen
		if (drawResponseSpot) responseSpot.draw(context); 

	}

	public boolean isDrawResponseSpot() {
		return drawResponseSpot;
	}
	public void setDrawResponseSpot(boolean drawResponseSpot) {
		this.drawResponseSpot = drawResponseSpot;
	}

	/**
	 * @return the dbUtil
	 */
	public SachDbUtil getDbUtil() {
		return dbUtil;
	}

	/**
	 * @param dbUtil the dbUtil to set
	 */
	public void setDbUtil(SachDbUtil dbUtil) {
		this.dbUtil = dbUtil;
	}
	
	
	
}
