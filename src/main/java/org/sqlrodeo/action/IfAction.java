package org.sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.ExecutionException;
import org.sqlrodeo.implementation.JexlEvaluationException;
import org.w3c.dom.Node;

/**
 * Action that implements the &lt;if&gt; element. This action evaluates the
 * given condition attribute, and will only execute its children if the
 * condition evaluates to true.
 */
public final class IfAction extends BaseAction {

	/** Logger */
	Logger log = LoggerFactory.getLogger(IfAction.class);

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The XML Node to which this action is attached.
	 */
	public IfAction(Node node) {
		super(node);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
	 */
	@Override
	public void execute(ExecutionContext context) {

		String condition = getNode().getAttribute("condition");

		try {
			boolean conditionIsTrue = context.evaluateBoolean(condition);
			if (log.isDebugEnabled()) {
				log.debug(String.format(
						"execute: condition, result".replaceAll(", ", "=%s, ")
								+ "=%s", condition, conditionIsTrue));
			}

			if (conditionIsTrue) {
				executeChildren(context);
			}
		} catch (JexlEvaluationException e) {
			throw new ExecutionException(this, e.getMessage(), e);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see sqlrodeo.Action#validate()
	 */
	@Override
	public void validate() {
		// Nothing to do. XSD enforces existence of condition attribute.
	}
}
