package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;

public final class AssignAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(AssignAction.class);

    public AssignAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ExecutionContext context) {

        try {
            String id = getNode().getAttribute("id");
            String value = context.substitute(getNode().getAttribute("value"));
            context.put(id, context.evaluate(value));
        } catch(JexlEvaluationException e) {
            throw new ExecutionException(this, e);
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
