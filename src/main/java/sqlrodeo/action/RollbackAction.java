package sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;

/**
 * Action that issues a rollback command to the given Connection.
 * 
 * @see java.sql.Connection
 */
public final class RollbackAction extends BaseAction {

    /** Logger */
    Logger log = LoggerFactory.getLogger(RollbackAction.class);

    /**
     * Constructor.
     * 
     * @param node The XML Node to which this action is attached.
     */
    public RollbackAction(Node node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext context) throws SQLException {
        String connectionId = getNode().getAttribute("connection-id");
        Connection conn = (Connection)context.get(connectionId);
        if(conn == null) {
            throw new ExecutionException(this, "Connection not found: " + connectionId);
        }

        conn.rollback();
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#validate()
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }
}
