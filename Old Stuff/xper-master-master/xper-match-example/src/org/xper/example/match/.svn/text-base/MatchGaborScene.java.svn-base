package org.xper.example.match;

import java.util.ArrayList;
import java.util.List;

import org.xper.drawing.Context;
import org.xper.experiment.ExperimentTask;
import org.xper.match.AbstractMatchTaskScene;
import org.xper.match.vo.MatchTrialContext;
import org.xper.rfplot.GaborSpec;
import org.xper.rfplot.RFPlotGaborObject;

public class MatchGaborScene extends AbstractMatchTaskScene {
	
	List<RFPlotGaborObject> objects = new ArrayList<RFPlotGaborObject>();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		RFPlotGaborObject.initGL();
	}

	public void setTask(ExperimentTask task) {
		objects.clear();
		MatchGaborSpec spec = MatchGaborSpec.fromXml(task.getStimSpec());
		for (int i = 0; i < spec.getObjectSpecCount(); i ++) {
			GaborSpec g = spec.getObjectSpec(i);
			RFPlotGaborObject obj = new RFPlotGaborObject();
			obj.setSpec(g.toXml());
			objects.add(obj);
		}
	}

	public void drawStimulus(Context context) {
		MatchTrialContext matchContext = (MatchTrialContext)context;
		
		int index = matchContext.getSlideIndex();
		if (index >= 0 && index < objects.size()) {
			RFPlotGaborObject obj = objects.get(index);
			obj.draw(matchContext);
		}
	}
	
}
