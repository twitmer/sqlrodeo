package sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;

public final class CommitAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(CommitAction.class);

    public CommitAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ExecutionContext context) throws SQLException {

        String connectionId = getNode().getAttribute("id");
        Connection conn = (Connection)context.get(connectionId);
        if(conn == null) {
            throw new ExecutionException(this, "Connection not found: " + connectionId);
        }

        conn.commit();
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }
}
