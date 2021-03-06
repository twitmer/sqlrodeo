package org.sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.JexlEvaluationException;
import org.sqlrodeo.implementation.ValidationException;
import org.sqlrodeo.util.StringUtils;
import org.w3c.dom.Node;

public final class ScriptAction extends BaseAction {

	Logger log = LoggerFactory.getLogger(ScriptAction.class);

	/**
	 * Constructor.
	 * 
	 * @param node
	 *            The XML Node to which this action is attached.
	 */
	public ScriptAction(Node node) {
		super(node);
	}

	@Override
	public void execute(ExecutionContext context)
			throws JexlEvaluationException {

		String nodeText = getNode().getTextContent();

		log.debug("Evaluating: " + nodeText);
		Object result = context.evaluateScript(nodeText);
		log.debug("result: "
				+ result
				+ (result != null ? ", class=" + result.getClass().getName()
						: ""));
	}

	@Override
	public void validate() {
		String nodeText = getNode().getTextContent();
		if (StringUtils.isEmpty(nodeText)) {
			throw new ValidationException(this, "No text in script node: "
					+ toString());
		}
	}
}
