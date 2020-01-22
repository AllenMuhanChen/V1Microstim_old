package org.xper.example.fixchg;

import org.xper.experiment.StimSpecGenerator;

public class FixChgGaborSpecGenerator implements StimSpecGenerator {

	public static FixChgGaborSpec generate () {
		FixChgGaborSpec g = new FixChgGaborSpec();
		g.setXCenter(0);
		g.setYCenter(0);
		g.setSize(100);
		g.setOrientation(Math.random() * Math.PI);
		g.setFrequency(0.02);
		g.setPhase(Math.PI);
		g.setAnimation(true);
		g.setStereo(Math.random() > 0.5);
		return g;
	}

	public String generateStimSpec() {
		return FixChgGaborSpecGenerator.generate().toXml();
	}

}
