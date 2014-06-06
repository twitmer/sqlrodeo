package sqlrodeo.version;

import static org.junit.Assert.fail;

import org.junit.Test;

import sqlrodeo.implementation.ExecutionContext;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.JexlService;

public class TestVersionWithJexl {

    @Test
    public void testIt() {

	JexlService jexl = new JexlService();

	ExecutionContext context = new ExecutionContext();
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
