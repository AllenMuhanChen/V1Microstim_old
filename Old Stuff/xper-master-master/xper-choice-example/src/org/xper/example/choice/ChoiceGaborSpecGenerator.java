package org.xper.example.choice;

import org.xper.experiment.StimSpecGenerator;

public class ChoiceGaborSpecGenerator implements StimSpecGenerator {
	public static ChoiceGaborSpec generate () {
		ChoiceGaborSpec g = new ChoiceGaborSpec();
		g.object1.setXCenter(0);
		g.object1.setYCenter(0);
		g.object1.setSize(100);
		g.object1.setOrientation(Math.random() * Math.PI);
		g.object1.setFrequency(0.02);
		g.object1.setPhase(Math.PI);
		g.object1.setAnimation(true);
		g.reward1 = 1;
		
		g.object2.setXCenter(0);
		g.object2.setYCenter(0);
		g.object2.setSize(50);
		g.object2.setOrientation(Math.random() * Math.PI);
		g.object2.setFrequency(0.02);
		g.object2.setPhase(Math.PI);
		g.object2.setAnimation(true);
		g.reward2 = 2;
		return g;
	}

	public String generateStimSpec() {
		return ChoiceGaborSpecGenerator.generate().toXml();
	}
}
