package sqlrodeo.action;

import java.io.IOException;
import java.io.StringReader;
import java.util.Hashtable;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.StringRefAddr;
import javax.naming.spi.ObjectFactory;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.util.StringUtils;

/**
 * Action typically used for creating or obtaining a DataSource from JNDI.
 */
public final class ObjectFactoryAction extends BaseAction {

    /** Logger */
    private static final Logger log = LoggerFactory.getLogger(ObjectFactoryAction.class);

    /**
     * Constructor.
     * 
     * @param node
     */
    public ObjectFactoryAction(Node node) {
        super(node);
    }

    /**
     * Attempt to retrieve an object by name from JNDI.
     * 
     * @param jndiName Name of object within the JNDI context.
     * @return The object, if found, otherwise null.
     */
    private Object retrieveJndiObject(String jndiName) {

        if(jndiName == null || StringUtils.isEmpty(jndiName)) {
            return null;
        }

        try {
            Context initialContext = new InitialContext();
            Object jndiObject = initialContext.lookup("java:comp/env/" + jndiName);

            if(jndiObject != null && jndiObject instanceof DataSource) {
                return jndiObject;
            }
        } catch(NamingException e) {
            log.debug("Ignoring error from JNDI lookup: " + e.getMessage() + ", " + e.getClass().getName());
        }

        // Fallthrough
        return null;
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext context) throws Exception {

        // Attempt to retrieve the object from JNDI. If this succeeds, we can put it in the context and return immediately.
        String jndiName = context.substitute(getNode().getAttribute("name"));
        Object theObject = retrieveJndiObject(jndiName);
        if(theObject != null) {
            log.debug("JNDI object named '" + jndiName + "' resolved to instance of " + theObject.getClass().getName());
            context.put(getNode().getAttribute("id"), theObject);
            return;
        }

        // Create and invoke an instance of the ObjectFactory.
        ObjectFactory objectFactory = (ObjectFactory)Class.forName(context.substitute(getNode().getAttribute("factoryClassName"))).newInstance();
        theObject = invokeObjectFactory(context, objectFactory);
        log.debug("ObjectFactory created instance of " + theObject.getClass().getName());
        context.put(getNode().getAttribute("id"), theObject);
    }

    /**
     * Invoke the ObjectFactory.
     * 
     * @param context
     * @param objectFactory
     * @return The product of invoking the ObjectFactory's getObjectInstance() method.
     * @throws JexlEvaluationException
     * @throws IOException
     * @throws NamingException
     * @throws Exception
     */
    private Object invokeObjectFactory(ExecutionContext context, ObjectFactory objectFactory) throws JexlEvaluationException,
            IOException, NamingException, Exception {

        if(log.isDebugEnabled()) {
            log.debug(String.format("invokeObjectFactory: context, objectFactory".replaceAll(", ", "=%s, ") + "=%s", context,
                    objectFactory));
        }

        // Expand text properties, and store in the Reference.
        Properties props = new Properties();
        String text = context.substitute(getNode().getTextContent());
        if(text != null) {
            props.load(new StringReader(text));
        }
        log.info("ObjectFactory Props: " + props);
        Reference ref = new Reference(context.substitute(getNode().getAttribute("objectClassName")));
        for(String key : props.stringPropertyNames()) {
            ref.add(new StringRefAddr(key, props.getProperty(key)));
        }

        // Define other variables we don't actually care about.
        InitialContext initialContext = new InitialContext();
        Name name = null;
        Hashtable<?, ?> env = new Hashtable<>();

        // Invoke the ObjectFactory to create the desired object.
        Object theObject = objectFactory.getObjectInstance(ref, name, initialContext, env);
        return theObject;
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#validate()
     */
    @Override
    public void validate() {
        // Nothing to do. The XSD enforces things well enough.
    }
}
