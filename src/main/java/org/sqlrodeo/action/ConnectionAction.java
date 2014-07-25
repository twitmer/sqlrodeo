package org.sqlrodeo.action;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.ExecutionException;
import org.sqlrodeo.util.StringUtils;
import org.w3c.dom.Node;

public final class ConnectionAction extends BaseAction {

	Logger log = LoggerFactory.getLogger(ConnectionAction.class);

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The XML Node to which this action is attached.
	 */
	public ConnectionAction(Node node) {
		super(node);
	}

	@Override
	public void execute(ExecutionContext context) throws SQLException {

		String dataSourceId = getNode().getAttribute("datasource-id");
		String id = getNode().getAttribute("id");

		DataSource ds = (DataSource) context.get(dataSourceId);
		if (ds == null) {
			throw new ExecutionException(this, "Datasource not found: "
					+ dataSourceId);
		}

		Connection conn = ds.getConnection();

		// Update autocommit setting, if needed.
		String autocommitStr = getNode().getAttribute("autocommit");
		if (!StringUtils.isEmpty(autocommitStr)) {
			boolean desiredAutocommitValue = Boolean
					.parseBoolean(autocommitStr);
			if (conn.getAutoCommit() != desiredAutocommitValue) {
				conn.setAutoCommit(desiredAutocommitValue);
				if (conn.getAutoCommit() != desiredAutocommitValue) {
					throw new ExecutionException(this,
							"Could not turn autocommit to "
									+ desiredAutocommitValue
									+ " for connection " + id);
				}
			}
		}

		// Put the connection into the context.
		context.put(id, conn);
	}

	/**
	 * Default implementation of validate(). Does nothing.
	 */
	@Override
	public void validate() {
		// Nothing to do here.
	}
}
