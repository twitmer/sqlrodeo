package sqlrodeo.action;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.util.StringUtils;

public final class BasicDatasourceAction extends BaseAction {

    private static final Logger log = LoggerFactory.getLogger(BasicDatasourceAction.class);

    public BasicDatasourceAction(Node node) {
        super(node);
    }

    private DataSource buildBasicDatasource(Properties props) throws Exception {
        BasicDataSource bds = BasicDataSourceFactory.createDataSource(props);
        log.debug("DataSource: " + bds);
        return bds;
    }

    private DataSource buildJndiConnection(String jndiName) throws NamingException {
        Context initCtx = new InitialContext();
        Object jndiObject = initCtx.lookup("java:comp/env/" + jndiName);

        if(jndiObject != null && jndiObject instanceof DataSource) {
            return (DataSource)jndiObject;
        } else {
            throw new ExecutionException(this,
                    "Cannot create connection with jndiName=" + jndiName);
        }
    }

    @Override
    public void execute(ISqlRodeoContext context) throws Exception {
        String id = getNode().getAttribute("id");

        DataSource dataSource = null;

        String jndiName = context.substitute(getNode().getAttribute("name"));
        if(!StringUtils.isEmpty(jndiName)) {
            try {
                dataSource = buildJndiConnection(jndiName);
                log.debug("Datasource " + jndiName + " resolved.");
            } catch(Exception e) {
                log.debug("Datasource " + jndiName + " failed to resolve: " + e.getMessage());
            }
        }

        if(dataSource == null) {

            Properties props = new Properties();

            Map<String, String> attrs = getNode().getAttributesAsMap();
            for(Entry<String, String> entry : attrs.entrySet()) {
                props.setProperty(entry.getKey(), context.substitute(entry.getValue()));
            }
            log.debug("Building datasource with props: " + props);
            dataSource = buildBasicDatasource(props);

            // Since we created this datasource, we'll take responsibility for
            // closing it.
            context.pushCloseAction(new CloseBasicDatasourceAction(getNode()));
        }

        if(dataSource == null) {
            throw new ExecutionException(this, "Cannot create datasource from: "
                    + toString());
        }

        context.put(id, dataSource);
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }

}
