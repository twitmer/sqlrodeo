/**
 * 
 */
package sqlrodeo.action;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;

import javax.sql.DataSource;

import org.easymock.EasyMock;
import org.junit.Test;
import org.w3c.dom.Element;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionContextImplementation;
import sqlrodeo.implementation.ExecutionException;

/**
 * Test for the ConnectionAction class.
 */
public class TestConnectionAction {

    /**
     * Happy path test method for {@link sqlrodeo.action.ConnectionAction#execute(sqlrodeo.ExecutionContext)}.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<connection id=\"me\" datasource-id=\"theDs\" autocommit=\"true\"/>").getDocumentElement();

        // Given: a mock Connection.
        Connection mockConnection = EasyMock.createMock(Connection.class);
        EasyMock.expect(mockConnection.getAutoCommit()).andReturn(true);
        EasyMock.replay(mockConnection);

        // Given: a mock DataSource.
        DataSource mockDataSource = EasyMock.createMock(DataSource.class);
        EasyMock.expect(mockDataSource.getConnection()).andReturn(mockConnection);
        EasyMock.replay(mockDataSource);

        // Given: The ConnectionAction;
        Action action = new ConnectionAction(node);

        // Expect: validation to succeed.
        action.validate();

        // Given: the Execution context with the expected datasource.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("theDs", mockDataSource);

        // When: ConnectionAction is executed.
        action.execute(context);

        // Then: DataSource was invoked as expected.
        EasyMock.verify(mockDataSource);

        // And: The Connection was invoked as expected.
        EasyMock.verify(mockConnection);
    }

    /**
     * Happy-ish path test method for {@link sqlrodeo.action.ConnectionAction#execute(sqlrodeo.ExecutionContext)}. This version
     * changes the autocommit property of the connection.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_changeTheAutocommit() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<connection id=\"me\" datasource-id=\"theDs\" autocommit=\"true\"/>").getDocumentElement();

        // Given: a mock Connection.
        Connection mockConnection = EasyMock.createMock(Connection.class);

        // 1st time through: return undesirable value for autocommit.
        EasyMock.expect(mockConnection.getAutoCommit()).andReturn(false);

        // Expect ConnectionAction to change the autocommit property.
        mockConnection.setAutoCommit(true);
        EasyMock.expectLastCall();

        // 2nd time through: return desirable value for autocommit.
        EasyMock.expect(mockConnection.getAutoCommit()).andReturn(true).times(1);

        EasyMock.replay(mockConnection);

        // Given: a mock DataSource.
        DataSource mockDataSource = EasyMock.createMock(DataSource.class);
        EasyMock.expect(mockDataSource.getConnection()).andReturn(mockConnection);
        EasyMock.replay(mockDataSource);

        // Given: The ConnectionAction;
        Action action = new ConnectionAction(node);

        // Expect: validation to succeed.
        action.validate();

        // Given: the Execution context with the expected datasource.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("theDs", mockDataSource);

        // When: ConnectionAction is executed.
        action.execute(context);

        // Then: DataSource was invoked as expected.
        EasyMock.verify(mockDataSource);

        // And: The Connection was invoked as expected.
        EasyMock.verify(mockConnection);
    }

    /**
     * Sad path test to test what happens when the action cannot change the autocommit property of the Connection.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_cannotChangeTheAutocommit() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<connection id=\"me\" datasource-id=\"theDs\" autocommit=\"true\"/>").getDocumentElement();

        // Given: a mock Connection that is locked to autocommit=false.
        Connection mockConnection = EasyMock.createMock(Connection.class);
        EasyMock.expect(mockConnection.getAutoCommit()).andReturn(false).times(2);
        mockConnection.setAutoCommit(true);
        EasyMock.expectLastCall();
        EasyMock.replay(mockConnection);

        // Given: a mock DataSource.
        DataSource mockDataSource = EasyMock.createMock(DataSource.class);
        EasyMock.expect(mockDataSource.getConnection()).andReturn(mockConnection);
        EasyMock.replay(mockDataSource);

        // Given: The ConnectionAction;
        Action action = new ConnectionAction(node);

        // Expect: validation to succeed.
        action.validate();

        // Given: the Execution context with the expected datasource.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("theDs", mockDataSource);

        // When: ConnectionAction is executed.
        try {
            action.execute(context);
            fail("Exception should have been thrown.");
        } catch(ExecutionException e) {
            assertTrue(e.getMessage().contains("Could not turn autocommit"));
            assertTrue(e.getMessage().contains("Url=file:"));
            assertTrue(e.getMessage().contains("lineNumber="));
        }

        // Then: DataSource was invoked as expected.
        EasyMock.verify(mockDataSource);

        // And: The Connection was invoked as expected.
        EasyMock.verify(mockConnection);
    }

    /**
     * Sad path test method for {@link sqlrodeo.action.ConnectionAction#execute(sqlrodeo.ExecutionContext)}. This version fails to
     * find the DataSource in the context.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_datasource_not_found() throws Exception {

        // Given: An XML node for the action
        Element node = XmlFactory.build("<connection id=\"me\" datasource-id=\"theDs\" autocommit=\"true\"/>").getDocumentElement();

        // Given: The ConnectionAction;
        Action action = new ConnectionAction(node);

        // Expect: validation to succeed.
        action.validate();

        // Given: the Execution context with the expected datasource.
        ExecutionContext context = new ExecutionContextImplementation();

        // When: ConnectionAction is executed.
        try {
            action.execute(context);
            fail("Exception should have been thrown.");
        } catch(ExecutionException e) {
            assertTrue(e.getMessage().contains("Datasource not found: theDs"));
            assertTrue(e.getMessage().contains("Url=file:"));
            assertTrue(e.getMessage().contains("lineNumber="));
        }
    }
    
    /**
     * Test for when autocommit is not specified in the XML.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_autocommit_not_specified() throws Exception {

        // Given: An XML node for the action where autocommit is not specified.
        Element node = XmlFactory.build("<connection id=\"me\" datasource-id=\"theDs\" />").getDocumentElement();

        // Given: a mock Connection.
        Connection mockConnection = EasyMock.createMock(Connection.class);
        EasyMock.replay(mockConnection);

        // Given: a mock DataSource.
        DataSource mockDataSource = EasyMock.createMock(DataSource.class);
        EasyMock.expect(mockDataSource.getConnection()).andReturn(mockConnection);
        EasyMock.replay(mockDataSource);

        // Given: The ConnectionAction;
        Action action = new ConnectionAction(node);

        // Expect: validation to succeed.
        action.validate();

        // Given: the Execution context with the expected datasource.
        ExecutionContext context = new ExecutionContextImplementation();
        context.put("theDs", mockDataSource);

        // When: ConnectionAction is executed.
        action.execute(context);

        // Then: DataSource was invoked as expected.
        EasyMock.verify(mockDataSource);

        // And: The Connection was invoked as expected.
        EasyMock.verify(mockConnection);
    }

}
