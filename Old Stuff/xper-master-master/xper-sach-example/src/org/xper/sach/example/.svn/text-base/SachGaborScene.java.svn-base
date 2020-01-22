package org.xper.sach.example;

import java.util.ArrayList;
import java.util.List;

import org.xper.Dependency;
import org.xper.drawing.Context;
import org.xper.drawing.GLUtil;
import org.xper.drawing.RGBColor;
import org.xper.drawing.object.Rectangle;
import org.xper.experiment.ExperimentTask;
import org.xper.rfplot.GaborSpec;
import org.xper.rfplot.RFPlotGaborObject;
import org.xper.sach.AbstractSachTaskScene;
import org.xper.sach.SachTrialExperiment;
import org.xper.sach.util.SachExperimentUtil;
import org.xper.sach.vo.SachTrialContext;

public class SachGaborScene extends AbstractSachTaskScene {
	
	@Dependency
	SachTrialExperiment experiment;
	
	Rectangle rect = new Rectangle(100, 10);
	
	List<RFPlotGaborObject> objects = new ArrayList<RFPlotGaborObject>();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		RFPlotGaborObject.initGL();
	}

	public void setTask(ExperimentTask task) {
		objects.clear();
		SachGaborSpec spec = SachGaborSpec.fromXml(task.getStimSpec());
		for (int i = 0; i < spec.getObjectSpecCount(); i ++) {
			GaborSpec g = spec.getObjectSpec(i);
			RFPlotGaborObject obj = new RFPlotGaborObject();
			obj.setSpec(g.toXml());
			objects.add(obj);
		}
		experiment.setFirstSlideISI(250);
		experiment.setFirstSlideLength(750);
	}

	public void drawStimulus(Context context) {
		SachTrialContext c = (SachTrialContext)context;
		
		int index = c.getSlideIndex();
		if (index >= 0 && index < objects.size()) {
			RFPlotGaborObject obj = objects.get(index);
			obj.draw(c);
		}
		
		GLUtil.drawRectangle(rect, 0, 50, 0, 0f, 1f, 0f);
	}

	protected void drawTargetObjects(Context context) {
		SachTrialContext c = (SachTrialContext)context;
		// change to draw the target in any way you want
		GLUtil.drawRectangle(rect, 0, 50, 0, 0f, 1f, 0f);
		SachExperimentUtil.drawTargetEyeWindow(c.getRenderer(), c.getTargetPos(), c.getTargetEyeWindowSize(), new RGBColor(1f, 1f, 0f));
	}
	
	protected void drawCustomBlank(Context context) {
		// draw your customized blank screen
		GLUtil.drawRectangle(rect, 0, 50, 0, 0f, 1f, 0f);
	}

	public SachTrialExperiment getExperiment() {
		return experiment;
	}

	public void setExperiment(SachTrialExperiment experiment) {
		this.experiment = experiment;
	}
}
