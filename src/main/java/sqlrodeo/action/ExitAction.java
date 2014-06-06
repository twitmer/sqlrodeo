package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.IExecutionContext;

public final class ExitAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(ExitAction.class);

    public ExitAction(Node node) {
        super(node);
    }

    @Override
    public void execute(IExecutionContext context) {
        throw new ExitException();
    }

    @Override
    public void validate() {
        // Nothing to do here.
    }
}
