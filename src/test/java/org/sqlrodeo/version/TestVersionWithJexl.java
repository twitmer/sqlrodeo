package org.sqlrodeo.version;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.sqlrodeo.implementation.ExecutionContextImplementation;
import org.sqlrodeo.implementation.JexlEvaluationException;
import org.sqlrodeo.implementation.JexlService;
import org.sqlrodeo.version.Version;

public class TestVersionWithJexl {

	@Test
	public void testIt() {

		JexlService jexl = new JexlService();

		ExecutionContextImplementation context = new ExecutionContextImplementation();
		context.put("version", new Version("1.0.1"));

		try {
			// jexl.evaluate("version.lt('1.0.0')", context);
			jexl.evaluate("version.lessThan('1.0.0')", context);
		} catch (JexlEvaluationException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
}
