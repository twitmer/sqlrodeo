package sqlrodeo.action;

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
import sqlrodeo.util.StringUtils;

public final class ObjectFactoryAction extends BaseAction {

    private static final Logger log = LoggerFactory.getLogger(ObjectFactoryAction.class);

    public ObjectFactoryAction(Node node) {
        super(node);
    }

    private DataSource buildJndiConnection(String jndiName) {
        try {
            Context initCtx = new InitialContext();
            Object jndiObject = initCtx.lookup("java:comp/env/" + jndiName);

            if(jndiObject != null && jndiObject instanceof DataSource) {
                return (DataSource)jndiObject;
            }
        } catch(NamingException e) {
            log.debug("Ignoring error from JNDI lookup: " + e.getMessage() + ", " + e.getClass().getName());
        }

        // Fallthrough
        return null;
    }

    @Override
    public void execute(ExecutionContext context) throws Exception {

        DataSource dataSource = null;

        String jndiName = context.substitute(getNode().getAttribute("name"));
        if(!StringUtils.isEmpty(jndiName)) {
            dataSource = buildJndiConnection(jndiName);
            if(dataSource != null) {
                log.debug("Datasource " + jndiName + " resolved.");
                return;
            }
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

        // Create an instance of the ObjectFactory.
        ObjectFactory of = (ObjectFactory)Class.forName(context.substitute(getNode().getAttribute("factoryClassName"))).newInstance();

        // Define other variables we don't actually care about.
        InitialContext initialContext = new InitialContext();
        Name name = null;
        Hashtable<?, ?> env = new Hashtable<>();

        // Invoke the ObjectFactory to create the desired object.
        Object product = of.getObjectInstance(ref, name, initialContext, env);

        // TODO: There is no "close()" method on DataSources, so it'll be up to
        // users of this library to correctly close a
        // datasource, if that's needed.

        context.put(getNode().getAttribute("id"), product);
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }

}
