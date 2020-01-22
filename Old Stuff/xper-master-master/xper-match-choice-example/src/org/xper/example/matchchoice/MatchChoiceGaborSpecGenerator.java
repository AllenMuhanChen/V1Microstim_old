package org.xper.example.matchchoice;

import org.xper.drawing.RGBColor;
import org.xper.experiment.StimSpecGenerator;

public class MatchChoiceGaborSpecGenerator implements StimSpecGenerator {
	public static MatchChoiceGaborSpec generate () {
		MatchChoiceGaborSpec g = new MatchChoiceGaborSpec();
		g.object1.setXCenter(0);
		g.object1.setYCenter(0);
		g.object1.setSize(100);
		g.object1.setOrientation(Math.random() * Math.PI);
		g.object1.setFrequency(0.02);
		g.object1.setPhase(Math.PI);
		g.object1.setAnimation(true);
		g.size1 = 2;
		g.distance1 = 10;
		g.color1 = new RGBColor(1, 0, 0);
		
		g.object2.setXCenter(0);
		g.object2.setYCenter(0);
		g.object2.setSize(50);
		g.object2.setOrientation(Math.random() * Math.PI);
		g.object2.setFrequency(0.02);
		g.object2.setPhase(Math.PI);
		g.object2.setAnimation(true);
		g.size2 = 1;
		g.distance2 = 5;
		g.color2 = new RGBColor(0, 1, 0);
		
		g.reward = 0;
		
		return g;
	}

	public String generateStimSpec() {
		return MatchChoiceGaborSpecGenerator.generate().toXml();
	}
}
