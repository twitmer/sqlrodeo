package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;

/**
 * Action that triggers execution to stop by throwing an ExitException.
 */
public final class ExitAction extends BaseAction {

    /** Logger */
    Logger log = LoggerFactory.getLogger(ExitAction.class);

    /**
     * Constructor.
     * 
     * @param node The XML Node to which this action is attached.
     */
    public ExitAction(Node node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext context) {
        throw new ExitException();
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#validate()
     */
    @Override
    public void validate() {
        // Nothing to do here.
    }
}
