package sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;

public final class RollbackAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(RollbackAction.class);

    public RollbackAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ISqlRodeoContext context) throws SQLException {
        String connectionId = getNode().getAttribute("connection-id");
        Connection conn = (Connection)context.get(connectionId);
        if(conn == null) {
            throw new ExecutionException(this, "Connection not found: "
                    + connectionId);
        }

        conn.rollback();
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }
}
