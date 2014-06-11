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
 * Test for the PropertiesAction class.
 * 
 * @see PropertiesAction
 */
public class TestExitAction {

    /**
     * Test method for {@link sqlrodeo.action.ExitAction#execute(sqlrodeo.ExecutionContext)} .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {
        // Happy path, properties content.
        Element node = XmlFactory.build("<exit/>").getDocumentElement();
        Action action = new ExitAction(node);

        action.validate();

        ExecutionContext context = new ExecutionContextImplementation();
        try {
            action.execute(context);

            fail("Expected to receive an ExitException");
        } catch(ExitException e) {
            // Happy path!
        }
    }
}
