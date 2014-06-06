package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;

public final class SqlRodeoAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(SqlRodeoAction.class);

    public SqlRodeoAction(Node node) {
	super(node);
    }

    @Override
    public void execute(ExecutionContext context) {
	log.debug("execute(): " + toString());
	executeChildren(context);
    }

    @Override
    public void validate() {
    }
}
