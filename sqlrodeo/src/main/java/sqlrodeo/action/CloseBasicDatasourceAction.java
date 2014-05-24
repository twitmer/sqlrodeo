package sqlrodeo.action;

import java.sql.SQLException;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.IExecutionContext;

public final class CloseBasicDatasourceAction extends BaseAction {

    private final String datasourceId;

    Logger log = LoggerFactory.getLogger(CloseBasicDatasourceAction.class);

    public CloseBasicDatasourceAction(Node node) {
        super(node);
        datasourceId = getNode().getAttribute("id");
    }

    @Override
    public void execute(IExecutionContext context) {
        // NOTE: Node will be null here.
        log.debug("CloseBasicDatasourceAction: " + datasourceId);
        BasicDataSource ds = (BasicDataSource)context.remove(datasourceId);

        if(ds == null) {
            log.debug("Datasource not found, doing nothing.");
            return;
        }

        if(!ds.isClosed()) {
            log.debug("Closing datasource " + datasourceId);
            try {
                ds.close();
            } catch(SQLException e) {
                // Ignore exception when closing datasource.
                log.debug("Ignoring exception when closing datasource: " + e.getMessage());
            }
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
