package org.xper.example.fixchg;

import org.apache.log4j.Logger;
import org.lwjgl.opengl.GL11;
import org.xper.Dependency;
import org.xper.classic.vo.TrialContext;
import org.xper.drawing.Context;
import org.xper.drawing.Coordinates2D;
import org.xper.drawing.RGBColor;
import org.xper.experiment.ExperimentTask;
import org.xper.fixchg.AbstractFixChgTaskScene;


public class FixChgGaborScene extends AbstractFixChgTaskScene {
	static Logger logger = Logger.getLogger(FixChgGaborScene.class);
	
	@Dependency
	double fixationSize;
	@Dependency
	RGBColor fixationColor;
	
	double disparity;
	
	FixChgGaborObject obj = new FixChgGaborObject();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		FixChgGaborObject.initGL();
	}

	@Override
	public void drawStimulus(Context context) {
		obj.draw(context);
	}

	@Override
	public void setTask(ExperimentTask task) {
		obj.setSpec(task.getStimSpec());
	}
	
	protected void drawVertexesInMm(Coordinates2D posInMm) {
		double z = 0.;
		
		GL11.glColor3f(fixationColor.getRed(), fixationColor.getGreen(), fixationColor.getBlue());
		
		GL11.glPushMatrix();
		GL11.glTranslated(posInMm.getX(), posInMm.getY(), z);
		GL11.glBegin(GL11.GL_QUADS);
			GL11.glVertex3d(-fixationSize/2., -fixationSize/2., z);
			GL11.glVertex3d(fixationSize/2., -fixationSize/2., z);
			GL11.glVertex3d(fixationSize/2., fixationSize/2., z);
			GL11.glVertex3d(-fixationSize/2., fixationSize/2., z);
		GL11.glEnd();
		GL11.glPopMatrix();
	}
	
	protected void drawVertexesInDegrees(Coordinates2D posInDegree) {
		double x = renderer.deg2mm(posInDegree.getX());
		double y = renderer.deg2mm(posInDegree.getY());
		Coordinates2D posInMm = new Coordinates2D(x,y);
		
		drawVertexesInMm(posInMm);
	}
	
	/*protected double getFixationDisparity (TrialContext context) {
		ExperimentTask task = context.getCurrentTask();
		if (task != null) {
			String xml = task.getStimSpec();
			Document doc = XmlUtil.parseSpec(xml);
			String value = doc.selectSingleNode("/StimSpec/fixationDisparity").getText();
			return Double.parseDouble(value);
		} else {
			return 0;
		}
	}*/

	@Override
	protected void drawFixation(TrialContext context) {
		// cannot use obj yet because setTask was not called yet
		if (context.getViewportIndex() == 0) {
			// image for one eye
			drawVertexesInDegrees(new Coordinates2D(0, 0));
		} else {
			// image for the other eye
			drawVertexesInDegrees(new Coordinates2D(disparity, 0));
		}
	}
	
	public void trialStart(TrialContext context) {
		super.trialStart(context);
		
		disparity = Math.random()*3.;
		if (logger.isInfoEnabled()) {
			logger.info("Fixation disparity: " + disparity);
		}
	}

	public double getFixationSize() {
		return fixationSize;
	}

	public void setFixationSize(double fixationSize) {
		this.fixationSize = fixationSize;
	}

	public RGBColor getFixationColor() {
		return fixationColor;
	}

	public void setFixationColor(RGBColor fixationColor) {
		this.fixationColor = fixationColor;
	}
}
