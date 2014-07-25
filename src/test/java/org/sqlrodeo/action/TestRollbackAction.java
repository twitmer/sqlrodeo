/**
 * 
 */
package org.sqlrodeo.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import org.easymock.EasyMock;
import org.junit.Test;
import org.sqlrodeo.Action;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.action.RollbackAction;
import org.sqlrodeo.implementation.ExecutionContextImplementation;
import org.sqlrodeo.implementation.ExecutionException;
import org.w3c.dom.Element;

/**
 * Test for the RollbackAction class.
 */
public class TestRollbackAction {

	/**
	 * Test method for
	 * {@link org.sqlrodeo.action.ExitAction#execute(org.sqlrodeo.ExecutionContext)}.
	 * This is a happy path test.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecute_happyPath() throws Exception {
		// Given: An XML node for the action
		Element node = XmlFactory.build("<rollback connection-id=\"myConn\"/>")
				.getDocumentElement();

		// Given: The RollbackAction;
		Action action = new RollbackAction(node);

		// Expect: validation to succeed.
		action.validate();

		// Given: A mock connection to rollback.
		Connection mockConnection = EasyMock.createMock(Connection.class);
		mockConnection.rollback();
		EasyMock.expectLastCall();
		EasyMock.replay(mockConnection);

		// Given: the Execution context containing the connection.
		ExecutionContext context = new ExecutionContextImplementation();
		context.put("myConn", mockConnection);

		// When: RollbackAction is executed.
		action.execute(context);

		// Then: the connection's rollback() method was invoked.
		EasyMock.verify(mockConnection);
	}

	/**
	 * Test method for
	 * {@link org.sqlrodeo.action.ExitAction#execute(org.sqlrodeo.ExecutionContext)}.
	 * This is a sad path that verifies what happens when the specified
	 * connection is not found in the ExecutionContext.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecute_nullConnection() throws Exception {

		// Given: An XML node for the action
		Element node = XmlFactory.build("<rollback connection-id=\"myConn\"/>")
				.getDocumentElement();

		// Given: The RollbackAction;
		Action action = new RollbackAction(node);

		// Expect: validation to succeed.
		action.validate();

		// Given: the Execution context without the connection.
		ExecutionContext context = new ExecutionContextImplementation();

		// When: RollbackAction is executed.
		try {
			action.execute(context);
			fail("Exception should have been thrown.");
		} catch (ExecutionException e) {
			// Then: An exception is thrown explaining the error.
			assertTrue(e.getMessage().contains("Connection not found"));
			assertTrue(e.getMessage().contains("Url=file:"));
			assertTrue(e.getMessage().contains("lineNumber="));
		}
	}
}
