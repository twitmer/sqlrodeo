package sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.util.StringUtils;

public final class ConnectionAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(ConnectionAction.class);

    public ConnectionAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ISqlRodeoContext context) throws SQLException {

        String dataSourceId = getNode().getAttribute("datasource-id");
        String id = getNode().getAttribute("id");

        boolean autocommit = true;
        String autocommitStr = getNode().getAttribute("autocommit");
        if(!StringUtils.isEmpty(autocommitStr)) {
            autocommit = Boolean.parseBoolean(autocommitStr);
        }

        DataSource ds = (DataSource)context.get(dataSourceId);
        if(ds == null) {
            throw new ExecutionException(this, "Datasource not found: "
                    + dataSourceId);
        }

        Connection conn = ds.getConnection();

        // Update autocommit setting, if needed.
        if(conn.getAutoCommit() != autocommit) {
            log.debug("Switching connection autocommit to " + autocommit);
            conn.setAutoCommit(autocommit);
            if(conn.getAutoCommit() != autocommit) {
                throw new ExecutionException(this, "Could not turn autocommit to "
                        + autocommit + " for connection " + id);
            }
            log.debug("connection " + id + " is autocommit?: " + conn.getAutoCommit());
        }

        context.put(id, conn);

        // Since we created this connection, we'll take responsibility for closing it.
        log.debug("Pushing close action");
        context.pushCloseAction(new CloseConnectionAction(getNode()));
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }
}
