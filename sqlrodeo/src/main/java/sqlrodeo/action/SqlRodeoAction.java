package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.IExecutionContext;

public final class SqlRodeoAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(SqlRodeoAction.class);

    public SqlRodeoAction(Node node) {
        super(node);
    }

    @Override
    public void execute(IExecutionContext context) {
        // if(isIfConditionTrue(context)) {
        log.debug("execute(): " + toString());
        executeChildren(context);
        // } else {
        // log.debug("Not running, because if-condition is false: " + toString());
        // }
    }

    @Override
    public void validate() {
    }
}
