package sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.IExecutionContext;

public final class CloseConnectionAction extends BaseAction {

    private final String connectionId;

    Logger log = LoggerFactory.getLogger(CloseConnectionAction.class);

    public CloseConnectionAction(Node node) {
	super(node);
	connectionId = getNode().getAttribute("id");
    }

    @Override
    public void execute(IExecutionContext context) throws SQLException {
	// NOTE: Node will be null here.
	log.debug("CloseConnectionAction: " + connectionId);
	Connection connection = (Connection) context.remove(connectionId);

	if (connection == null) {
	    log.debug("Connection not found, doing nothing.");
	    return;
	}

	// If connection isn't autocommit, rollback.
	// TODO: getAutoCommit() throws a SQL Exception?!
	if (!connection.getAutoCommit()) {
	    try {
		log.debug("Rolling back non-autocommit connection "
			+ connectionId);
		connection.rollback();
	    } catch (Exception e) {
		log.debug("Ignore error with rollback: " + e.getMessage());
	    }
	}

	// TODO: isClosed() throws a SQL Exception?!
	if (!connection.isClosed()) {
	    log.debug("Closing Connection " + connectionId);
	    try {
		connection.close();
	    } catch (Exception e) {
		log.debug("Ignore error with close: " + e.getMessage());
	    }
	}
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
	// Nothing to do here.
    }

}
