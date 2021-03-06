/**
 * 
 */
package org.sqlrodeo.action;

import static org.junit.Assert.*;

import org.junit.Test;
import org.sqlrodeo.Action;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.action.AssignAction;
import org.sqlrodeo.implementation.ExecutionContextImplementation;
import org.sqlrodeo.implementation.ExecutionException;
import org.w3c.dom.Element;

/**
 * Test for the AssignAction class.
 */
public class TestAssignAction {

	/**
	 * Happy path test method for
	 * {@link org.sqlrodeo.action.AssignAction#execute(org.sqlrodeo.ExecutionContext)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecute() throws Exception {

		// Given: An XML node for the action
		Element node = XmlFactory.build(
				"<assign id=\"someObject\" value=\"100\"/>")
				.getDocumentElement();

		// Given: The AssignAction;
		Action action = new AssignAction(node);

		// Expect: validation to succeed.
		action.validate();

		// Given: the Execution context with the expected datasource.
		ExecutionContext context = new ExecutionContextImplementation();

		// Expect: "someObject" is not in the context.
		assertNull(context.get("someObject"));

		// When: AssignAction is executed.
		action.execute(context);

		// Then: "someObject" is in the context.
		assertNotNull(context.get("someObject"));
	}

	/**
	 * Happy path test method for
	 * {@link org.sqlrodeo.action.AssignAction#execute(org.sqlrodeo.ExecutionContext)}.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecute_withJexlEvaluationException() throws Exception {

		// Given: An XML node for the action
		Element node = XmlFactory.build(
				"<assign id=\"someObject\" value=\"cannot be evaluated!\"/>")
				.getDocumentElement();

		// Given: The AssignAction;
		Action action = new AssignAction(node);

		// Expect: validation to succeed.
		action.validate();

		// Given: the Execution context with the expected datasource.
		ExecutionContext context = new ExecutionContextImplementation();

		// Expect: "someObject" is not in the context.
		assertNull(context.get("someObject"));

		// When: AssignAction is executed.
		try {
			action.execute(context);
			fail("Exception should have been thrown");
		} catch (ExecutionException e) {
			assertTrue(e.getMessage().contains(
					"Failed to evaluate: cannot be evaluated!"));
			assertTrue(e.getMessage().contains("Url=file:"));
			assertTrue(e.getMessage().contains("lineNumber="));
		}

		// Expect: "someObject" is still not in the context.
		assertNull(context.get("someObject"));
	}

}
