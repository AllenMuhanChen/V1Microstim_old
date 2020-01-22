package org.xper.example.choice;

import junit.framework.TestCase;

import org.springframework.config.java.context.JavaConfigApplicationContext;

public class JavaConfigTest extends TestCase {
	public void testInheritance () {
		JavaConfigApplicationContext context = new JavaConfigApplicationContext(ConfigThree.class);
		String b = (String)context.getBean("indirectRefBean");
		assertEquals("ConfigThree", b);
	}
}
