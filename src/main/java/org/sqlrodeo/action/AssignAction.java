package org.sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.ExecutionException;
import org.sqlrodeo.implementation.JexlEvaluationException;
import org.w3c.dom.Node;

public final class AssignAction extends BaseAction {

	Logger log = LoggerFactory.getLogger(AssignAction.class);

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The XML Node to which this action is attached.
	 */
	public AssignAction(Node node) {
		super(node);
	}

	@Override
	public void execute(ExecutionContext context) {

		try {
			String id = getNode().getAttribute("id");
			String value = context.substitute(getNode().getAttribute("value"));
			context.put(id, context.evaluate(value));
		} catch (JexlEvaluationException e) {
			throw new ExecutionException(this, e.getMessage(), e);
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
