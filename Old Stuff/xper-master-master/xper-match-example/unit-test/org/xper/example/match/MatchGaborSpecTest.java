package org.xper.example.match;

import org.xper.rfplot.GaborSpec;

import junit.framework.TestCase;

public class MatchGaborSpecTest extends TestCase {
	public void test () {
		MatchGaborSpec spec = new MatchGaborSpec();
		spec.addObjectSpec(new GaborSpec());
		System.out.println(spec.toXml());
	}
}
