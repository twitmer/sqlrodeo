package org.sqlrodeo.action;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.Action;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.ExecutionException;
import org.sqlrodeo.implementation.JexlEvaluationException;
import org.sqlrodeo.util.StringUtils;
import org.sqlrodeo.xml.NodeWrapper;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Base class that provides common functions for all Actions.
 */
public abstract class BaseAction implements Action {

	/** Logger */
	private static final Logger log = LoggerFactory.getLogger(BaseAction.class);

	/** The XML Node to which this action is attached. */
	private final NodeWrapper node;

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The XML Node to which this action is attached.
	 */
	public BaseAction(Node node) {
		this.node = new NodeWrapper(node);
	}

	private boolean conditionIsTrueOrEmpty(String condition,
			ExecutionContext context) throws JexlEvaluationException {

		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"conditionIsTrueOrEmpty: condition".replaceAll(", ",
							"=%s, ") + "=%s", condition));
		}

		// Assume false until proven otherwise.
		boolean isTrue = StringUtils.isEmpty(condition)
				|| context.evaluateBoolean(condition);

		if (log.isInfoEnabled()) {
			log.info("Condition '" + condition + "' is " + isTrue);
		}

		return isTrue;
	}

	/**
	 * Run the child actions of this action, based on the child nodes of this
	 * action's node.
	 * 
	 * @param context
	 *            The executionContext to use during execution.
	 */
	public void executeChildren(ExecutionContext context) {
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"executeChildren: node".replaceAll(", ", "=%s, ") + "=%s",
					getNode().toString()));
		}

		// Find each element and run it.
		List<Node> kids = getNode().getChildNodesAsList();
		for (int i = 0; i < kids.size(); ++i) {
			NodeWrapper kid = new NodeWrapper(kids.get(i));

			if (kids.get(i) instanceof Element) {
				Action action = (Action) kid.getUserData("action");
				if (log.isDebugEnabled()) {
					log.debug("Action is " + action + " for node: " + kid);
				}
				try {

					if (conditionIsTrueOrEmpty(
							action.getNode().getAttribute("if"), context)) {
						if (log.isInfoEnabled()) {
							log.info("Executing action: " + action.toString()
									+ " in " + action.resolveResourceUrl()
									+ ", line: " + action.resolveLineNumber());
						}
						action.execute(context);
					} else {
						if (log.isInfoEnabled()) {
							log.info("Skipping action: " + action.toString()
									+ " in " + action.resolveResourceUrl()
									+ ", line: " + action.resolveLineNumber()
									+ " because 'if' condition is false.");
						}
					}
				} catch (ExecutionException e) {
					throw e;
					// } catch(RuntimeException e) {
					// throw new ExecutionException(action, e.getMessage(), e);
				} catch (Exception e) {
					throw new ExecutionException(action, e.getMessage(), e);
				}
			}
		}
	}

	protected boolean getAttributeAsBoolean(String name, boolean defaultValue) {
		String valueAsStr = getNode().getAttribute(name);
		if (!StringUtils.isEmpty(valueAsStr)) {
			return Boolean.parseBoolean(valueAsStr);
		}

		return defaultValue;
	}

	protected Connection getConnection(ExecutionContext context) {

		NodeWrapper workingNode = getNode();
		String connId = workingNode.getAttribute("connection-id");
		if (log.isDebugEnabled()) {
			log.debug(String.format(
					"getConnection: connId".replaceAll(", ", "=%s, ") + "=%s",
					connId));
		}

		// If connId is specified, it better exist!
		if (!StringUtils.isEmpty(connId)) {
			Connection conn = (Connection) context.get(connId);
			if (conn == null) {
				throw new ExecutionException(this, "Connection not found: "
						+ connId);
			}
			return conn;
		}

		while (workingNode != null && StringUtils.isEmpty(connId)) {

			if (!StringUtils.isEmpty(connId)) {

				Connection conn = (Connection) context.get(connId);
				if (conn == null) {
					throw new ExecutionException(this, "Connection not found: "
							+ connId);
				}
				return conn;
			}

			if (workingNode.getParentNode() != null) {
				workingNode = new NodeWrapper(workingNode.getParentNode());
				connId = workingNode.getAttribute("connection-id");
			} else {
				workingNode = null;
			}
		}

		// Didn't find connection-id on parent node, so see if the context knows
		// which it is.
		try {
			return getDefaultConnection(context);
		} catch (ExecutionException e) {
			throw e;
		} catch (Exception e) {
			throw new ExecutionException(this, e.getMessage(), e);
		}
	}

	/**
	 * Find the default database connection. This only works if there is only
	 * one Connection in the context.
	 * 
	 * @return Existing connection object from context, but only if only 1
	 *         connection is found.
	 * @throws NotFoundException
	 *             If 0, 2, or more Connections exist in the context.
	 */
	private Connection getDefaultConnection(ExecutionContext context) {

		Iterator<Entry<String, Object>> iter = context.entrySet().iterator();
		while (iter.hasNext()) {
			Entry<String, Object> entry = (Entry<String, Object>) iter.next();
			Object value = entry.getValue();
			if (value != null && value instanceof Connection) {
				return (Connection) value;
			}
		}

		// Fallthrough: Could not find default connection.
		throw new IllegalArgumentException(
				"Could not identify default connecton.");
	}

	@Override
	public NodeWrapper getNode() {
		return node;
	}

	@Override
	public long resolveLineNumber() {
		return getNode().resolveLineNumber();
	}

	protected URL resolveRelativeUrl(String href) throws MalformedURLException,
			URISyntaxException {
		URL resourceUrl = getNode().resolveResourceUrl();
		return resourceUrl.toURI().resolve(".").resolve(href).toURL();
	}

	@Override
	public URL resolveResourceUrl() {
		return getNode().resolveResourceUrl();
	}

	@Override
	public String toString() {
		return this.getClass().getSimpleName() + ": " + getNode().toString();
	}
}
