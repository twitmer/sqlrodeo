package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;

/**
 * Action that implements the &lt;sql-rodeo&gt; element. This simply executes
 * all of its children. ("Phrasing!")
 */
public final class SqlRodeoAction extends BaseAction {

    /** Logger */
    Logger log = LoggerFactory.getLogger(SqlRodeoAction.class);

    /**
     * Constructor.
     * 
     * @param node
     *            The XML Node to which this action is attached.
     */
    public SqlRodeoAction(Node node) {
	super(node);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext context) {
	log.debug("execute(): " + toString());
	executeChildren(context);
    }

    /*
     * (non-Javadoc)
     * 
     * @see sqlrodeo.Action#validate()
     */
    @Override
    public void validate() {
    }
}
