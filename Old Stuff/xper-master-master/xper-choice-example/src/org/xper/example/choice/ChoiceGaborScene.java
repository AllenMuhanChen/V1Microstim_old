package org.xper.example.choice;

import org.xper.choice.AbstractChoiceTaskScene;
import org.xper.choice.vo.ChoiceTrialContext;
import org.xper.drawing.Context;
import org.xper.experiment.ExperimentTask;
import org.xper.rfplot.RFPlotGaborObject;

public class ChoiceGaborScene extends AbstractChoiceTaskScene {
	
	RFPlotGaborObject object1 = new RFPlotGaborObject();
	RFPlotGaborObject object2 = new RFPlotGaborObject();
	
	public void initGL(int w, int h) {
		super.initGL(w, h);
		RFPlotGaborObject.initGL();
	}

	public void setTask(ExperimentTask task) {
		ChoiceGaborSpec choiceSpec = ChoiceGaborSpec.fromXml(task.getStimSpec());
		object1.setSpec(choiceSpec.getObject1().toXml());
		object2.setSpec(choiceSpec.getObject2().toXml());
	}

	public void drawStimulus(Context context) {
		ChoiceTrialContext choiceContext = (ChoiceTrialContext)context;
		
		RFPlotGaborObject obj;
		if (choiceContext.getSlideIndex() == 0) {
			obj = object1;
		} else {
			obj = object2;
		}
		obj.draw(choiceContext);
	}
	
}
