/**
 * 
 */
package sqlrodeo.action;

import static org.junit.Assert.*;

import java.net.URL;

import org.easymock.EasyMock;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionContextImplementation;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.xml.NodeWrapper;

/**
 * Test for the IfAction class.
 */
public class TestIfAction {

    /**
     * Happy path test method for {@link sqlrodeo.action.IfAction#execute(sqlrodeo.ExecutionContext)}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<if condition=\"x eq 10\"><mockNode/></if>").getDocumentElement();

        // Given: the child node for the ifAction.
        Node child = node.getChildNodes().item(0);
        assertNotNull(child);

        // Given: the Execution context containing values that would make this true.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("x", Long.valueOf(10));

        // Given: a mock action in the child node.
        Action mockAction = EasyMock.createMock(Action.class);
        EasyMock.expect(mockAction.getNode()).andReturn(new NodeWrapper(child));
        EasyMock.expect(mockAction.resolveResourceUrl()).andReturn(new URL("file://"));
        EasyMock.expect(mockAction.resolveLineNumber()).andReturn(Long.valueOf(100));
        mockAction.execute(context);
        EasyMock.expectLastCall();
        EasyMock.replay(mockAction);

        // Given: the action is embedded into the node.
        child.setUserData("action", mockAction, null);

        // Given: The IfAction;
        Action action = new IfAction(node);

        // Expect: validation to succeed.
        action.validate();

        // When: IfAction is executed
        action.execute(context);

        // Then: the IfAction executes its children. ("Phrasing!")
        EasyMock.verify(mockAction);
    }

    /**
     * Sad path test method for {@link sqlrodeo.action.IfAction#execute(sqlrodeo.ExecutionContext)}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_sadPath() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<if condition=\"x eq 99\"><mockNode/></if>").getDocumentElement();

        // Given: the child node for the ifAction.
        Node child = node.getChildNodes().item(0);
        assertNotNull(child);

        // Given: the Execution context containing values that would make this false.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("x", Long.valueOf(10));

        // Given: a mock action in the child node that will NOT be invoked.
        Action mockAction = EasyMock.createMock(Action.class);
        EasyMock.replay(mockAction);

        // Given: the action is embedded into the node.
        child.setUserData("action", mockAction, null);

        // Given: The IfAction;
        Action action = new IfAction(node);

        // Expect: validation to succeed.
        action.validate();

        // When: IfAction is executed
        action.execute(context);

        // Then: the IfAction did NOT execute its children. ("Phrasing!")
        EasyMock.verify(mockAction);
    }

    /**
     * Sad path test method for {@link sqlrodeo.action.IfAction#execute(sqlrodeo.ExecutionContext)}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_jexlException() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<if condition=\"x x x\"><mockNode/></if>").getDocumentElement();

        // Given: the child node for the ifAction.
        Node child = node.getChildNodes().item(0);
        assertNotNull(child);

        // Given: the Execution context containing values that would make this false.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("x", Long.valueOf(10));

        // Given: a mock action in the child node that will NOT be invoked.
        Action mockAction = EasyMock.createMock(Action.class);
        EasyMock.replay(mockAction);

        // Given: the action is embedded into the node.
        child.setUserData("action", mockAction, null);

        // Given: The IfAction;
        Action action = new IfAction(node);

        // Expect: validation to succeed.
        action.validate();

        // When: IfAction is executed
        try {
            action.execute(context);
            fail("This should have thrown an exception");
        } catch(ExecutionException e) {
            assertSame(JexlEvaluationException.class.getName(), e.getCause().getClass().getName());
            assertTrue(e.getCause().getMessage().contains("Failed to evaluate: x x x"));
            assertTrue(e.getMessage().contains("Url=file:"));
            assertTrue(e.getMessage().contains("lineNumber="));
        }

        // Then: the IfAction did NOT execute its children. ("Phrasing!")
        EasyMock.verify(mockAction);
    }
}
