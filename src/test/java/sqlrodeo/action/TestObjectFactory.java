/**
 * 
 */
package sqlrodeo.action;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.spi.ObjectFactory;

import org.easymock.EasyMock;
import org.easymock.IAnswer;
import org.junit.Test;
import org.w3c.dom.Element;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionContextImplementation;

/**
 * Test for the ObjectFactoryAction class.
 * 
 * @see ObjectFactoryAction
 */
public class TestObjectFactory {

    /**
     * Test method for {@link sqlrodeo.action.ObjectFactoryAction#execute(sqlrodeo.ExecutionContext)}. This is a happy path test
     * where the object is found via JNDI lookup, so there's no need to invoke the ObjectFactory.
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_fromJNDI_happyPath() throws Exception {

        // Given: an XML node for the ObjectFactoryAction
        Element node = XmlFactory.build(
                "<objectFactory id=\"myDataSource\" name=\"someName\" factoryClassName=\"ignoredForJndi\" objectClassName=\"ignoredForJndi\" ></objectFactory>").getDocumentElement();

        // Given: the desired object to retrieve from JNDI
        Object theJndiObject = new Object();

        // Given: a mock JNDI context for retrieving the above object.
        Context mockContext = EasyMock.createMock(Context.class);
        EasyMock.expect(mockContext.lookup("java:comp/env/someName")).andReturn(theJndiObject);
        EasyMock.replay(mockContext);

        // Given: An ObjectFactoryAction based on the XML node and mock JNDI context
        Action action = new ObjectFactoryAction(node);
        action.validate();
        ((ObjectFactoryAction)action).setInitialContext(mockContext);

        // Given: an empty ExecutionContext.
        ExecutionContext context = new ExecutionContextImplementation();

        // Given: Before execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        // When: The ObjectFactoryAction is executed.
        action.execute(context);

        // Then: theJndiObject was found and placed into the ExecutionContext.
        assertNotNull(context.get("myDataSource"));
        assertSame(theJndiObject, context.get("myDataSource"));

        // Then: the mock context was invoked as expected.
        EasyMock.verify(mockContext);
    }

    /**
     * Test method for {@link sqlrodeo.action.ObjectFactoryAction#execute(sqlrodeo.ExecutionContext)} .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_fromJNDI_noJndi() throws Exception {

        // Given: an XML node for the ObjectFactoryAction WITHOUT a JNDI name.
        Element node = XmlFactory.build(
                "<objectFactory id=\"myDataSource\" factoryClassName=\"ignoredForJndi\" objectClassName=\"ignoredForJndi\" ></objectFactory>").getDocumentElement();

        // Given: a mock JNDI context. (Empty, and never invoked.)
        Context mockContext = EasyMock.createMock(Context.class);
        EasyMock.replay(mockContext);

        // Given: An ObjectFactoryAction based on the XML node and mock JNDI context
        Action action = new ObjectFactoryAction(node);
        action.validate();
        ((ObjectFactoryAction)action).setInitialContext(mockContext);

        // Given: an empty ExecutionContext.
        ExecutionContext context = new ExecutionContextImplementation();

        // Given: Before execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        // When: The ObjectFactoryAction is executed.
        try {
            action.execute(context);
            fail("Action should have thrown a ClassNotFoundException");
        } catch(ClassNotFoundException e) {
            assertTrue(e.getMessage().equals("ignoredForJndi"));
        }

        // Then: After execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        EasyMock.verify(mockContext);
    }

    /**
     * Test method for {@link sqlrodeo.action.ObjectFactoryAction#execute(sqlrodeo.ExecutionContext)} .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_jndi_errors() throws Exception {

        // Given: an XML node for the ObjectFactoryAction WITH a JNDI name that won't resolve.
        Element node = XmlFactory.build(
                "<objectFactory id=\"myDataSource\" name=\"someName\" factoryClassName=\"ignoredForJndi\" objectClassName=\"ignoredForJndi\" ></objectFactory>").getDocumentElement();

        // Given: a mock JNDI context. (Empty, and never invoked.)
        Context mockContext = EasyMock.createMock(Context.class);

        // First lookup returns null.
        EasyMock.expect(mockContext.lookup("java:comp/env/someName")).andReturn(null);

        // Next lookup throws a NamingException.
        EasyMock.expect(mockContext.lookup("java:comp/env/someName")).andAnswer(new IAnswer<Object>() {
            public Object answer() throws NamingException {
                throw new NamingException("Test Naming Exception");
            }
        });
        EasyMock.replay(mockContext);

        // Given: An ObjectFactoryAction based on the XML node and mock JNDI context
        Action action = new ObjectFactoryAction(node);
        action.validate();
        ((ObjectFactoryAction)action).setInitialContext(mockContext);

        // Given: an empty ExecutionContext.
        ExecutionContext context = new ExecutionContextImplementation();

        // Given: Before execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        // When: The ObjectFactoryAction is executed the first time.
        try {
            action.execute(context);
            fail("Action should have thrown a ClassNotFoundException");
        } catch(ClassNotFoundException e) {
            // Then: Action falls back to instantiating the specified ObjectFactory class.
            assertTrue(e.getMessage().equals("ignoredForJndi"));
        }

        // When: The ObjectFactoryAction is executed the next time.
        try {
            action.execute(context);
            fail("Action should have thrown a ClassNotFoundException");
        } catch(ClassNotFoundException e) {
            // Then: Action falls back to instantiating the specified ObjectFactory class.
            assertTrue(e.getMessage().equals("ignoredForJndi"));
        }

        // Then: After execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        EasyMock.verify(mockContext);
    }

    /**
     * Test method for {@link sqlrodeo.action.ObjectFactoryAction#execute(sqlrodeo.ExecutionContext)} .
     * 
     * @throws Exception
     */
    @Test
    public void testExecute_objectFactory() throws Exception {

        // Given: an XML node for the ObjectFactoryAction WITHOUT a JNDI name.
        Element node = XmlFactory.build(
                "<objectFactory id=\"myDataSource\" factoryClassName=\"sqlrodeo.action.TestObjectFactory$MockObjectFactory\" objectClassName=\"String\"></objectFactory>").getDocumentElement();

        // Given: a mock JNDI context. (Empty, and never invoked.)
        Context mockContext = EasyMock.createMock(Context.class);
        EasyMock.replay(mockContext);

        System.out.println("WANT: " + new MockObjectFactory().getClass().getName());
        // Given: An ObjectFactoryAction based on the XML node and mock JNDI context
        Action action = new ObjectFactoryAction(node);
        action.validate();
        ((ObjectFactoryAction)action).setInitialContext(mockContext);

        // Given: an empty ExecutionContext.
        ExecutionContext context = new ExecutionContextImplementation();

        // Given: Before execution, JNDI object is not in context.
        assertNull(context.get("myDataSource"));

        // When: The ObjectFactoryAction is executed
        action.execute(context);

        // Then: After execution, JNDI object is in context.
        assertNotNull(context.get("myDataSource"));

        EasyMock.verify(mockContext);
    }

    public static class MockObjectFactory implements ObjectFactory {

        @Override
        public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws Exception {
            return new String("Product of MockObjectFactory");
        }

    }

}
