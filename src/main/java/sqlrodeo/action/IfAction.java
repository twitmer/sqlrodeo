package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;

public final class IfAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(IfAction.class);

    public IfAction(Node node) {
	super(node);
    }

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
	    throw new ExecutionException(this, e);
	}
    }

    @Override
    public void validate() {
	// TODO: Require condition to be populated.
    }
}
