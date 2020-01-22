package org.xper.example.matchchoice;

import org.xper.matchchoice.vo.MatchChoiceTrialContext;
import org.xper.drawing.Context;
import org.xper.experiment.ExperimentTask;
import org.xper.matchchoice.AbstractMatchChoiceTaskScene;
import org.xper.rfplot.RFPlotGaborObject;

public class MatchChoiceGaborScene extends AbstractMatchChoiceTaskScene {
	
	RFPlotGaborObject object1 = new RFPlotGaborObject();
	RFPlotGaborObject object2 = new RFPlotGaborObject();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		RFPlotGaborObject.initGL();
	}

	public void setTask(ExperimentTask task) {
		MatchChoiceGaborSpec choiceSpec = MatchChoiceGaborSpec.fromXml(task.getStimSpec());
		object1.setSpec(choiceSpec.getObject1().toXml());
		object2.setSpec(choiceSpec.getObject2().toXml());
	}

	public void drawStimulus(Context context) {
		MatchChoiceTrialContext matchChoiceContext = (MatchChoiceTrialContext)context;
		
		RFPlotGaborObject obj;
		if (matchChoiceContext.getSlideIndex() == 0) {
			obj = object1;
		} else {
			obj = object2;
		}
		obj.draw(matchChoiceContext);
	}
	
}
