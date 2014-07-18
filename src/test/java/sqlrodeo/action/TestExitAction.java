/**
 * 
 */
package sqlrodeo.action;

import static org.junit.Assert.fail;

import org.junit.Test;
import org.w3c.dom.Element;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionContextImplementation;

/**
 * Test for the ExitAction class.
 */
public class TestExitAction {

	/**
	 * Happy path test method for
	 * {@link sqlrodeo.action.ExitAction#execute(sqlrodeo.ExecutionContext)}.
	 * 
	 * @throws Exception
	 */
	@Test(expected = ExitException.class)
	public void testExecute() throws Exception {
		// Given: An XML node for the action
		Element node = XmlFactory.build("<exit/>").getDocumentElement();

		// Given: The ExitAction;
		Action action = new ExitAction(node);

		// Expect: validation to succeed.
		action.validate();

		// Given: the Execution context.
		ExecutionContext context = new ExecutionContextImplementation();

		// When: ExitAction is executed, an ExitException is thrown.
		action.execute(context);

		// Expect: We never reach this line.
		fail("Expected to receive an ExitException");
	}
}
