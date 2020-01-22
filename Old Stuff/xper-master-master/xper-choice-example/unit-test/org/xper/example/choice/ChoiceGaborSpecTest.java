package org.xper.example.choice;

import org.xper.example.choice.ChoiceGaborSpec;

import junit.framework.TestCase;


public class ChoiceGaborSpecTest extends TestCase {
	public void testXml () {
		ChoiceGaborSpec g1 = new ChoiceGaborSpec();
		String xml = g1.toXml();
		//System.out.println(xml);
		ChoiceGaborSpec g2 = ChoiceGaborSpec.fromXml(xml);
		assertEquals(g1.getReward1(), g2.getReward1());
	}
}
