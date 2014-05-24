package sqlrodeo.action;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import sqlrodeo.IExecutionContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.util.StringUtils;

public final class QueryAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(QueryAction.class);

    public QueryAction(Node node) {
        super(node);
    }

    @Override
    public void execute(IExecutionContext context) throws SQLException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("execute: node".replaceAll(", ", "=%s, ") + "=%s", toString()));
        }
        String queryString = getNode().getFirstChild().getNodeValue();
        String[] publishAs = getPublishAs(getNode().getAttribute("publish-as"));

        boolean substitute = true;
        String substituteStr = getNode().getAttribute("substitute");
        if(!StringUtils.isEmpty(substituteStr)) {
            substitute = Boolean.parseBoolean(substituteStr);
        }

        String rowNumId = getNode().getAttribute("rownum");

        List<Node> queryChildren = getNode().getChildNodesAsList();
        queryChildren.remove(0);

        // Variable substitution (i.e. '${release}' -> '1')
        if(substitute) {
            try {
                queryString = context.substitute(queryString);
            } catch(JexlEvaluationException e) {
                throw new ExecutionException(this, e);
            }
        }

        Connection connection = getConnection(context);
        Statement stmt = connection.createStatement();
        log.info("Executing statement: " + queryString);

        long resultCount = 0;
        long rowCount = 0;
        try {
            final String qString = queryString;
            ResultSet result = stmt.executeQuery(qString);
            try {
                while(result.next()) {
                    publishRow(queryChildren, result, context, publishAs);
                    if(!StringUtils.isEmpty(rowNumId)) {
                        context.put(rowNumId, rowCount);
                    }
                    ++resultCount;
                }

            } finally {
                try {
                    result.close();
                } catch(Exception e) {
                    log.debug("Ignoring error closing resultSet: " + e.getMessage());
                }
            }
        } finally {
            try {
                stmt.close();
            } catch(Exception e) {
                log.debug("Ignoring error closing statement: " + e.getMessage());
            }
        }

        // Optionally publish the number of rows retrieved.
        String rowCountId = getNode().getAttribute("rowcount");
        if(!StringUtils.isEmpty(rowCountId)) {
            context.put(rowCountId, resultCount);
        }
    }

    ResultSet executeQuery(Connection connection, String sqlText) throws SQLException {
        Statement stmt = connection.createStatement();
        log.info("Executing statement: " + sqlText);

        ResultSet result = stmt.executeQuery(sqlText);
        try {
            stmt.close();
        } catch(Exception e) {
            log.debug("Ignoring problem closing statement: " + e.getMessage());
        }
        return result;
    }

    String[] getPublishAs(String publishAs) {

        if(StringUtils.isEmpty(publishAs)) {
            return new String[0];
        }

        String[] result = publishAs.split(",");
        for(int i = 0; i < result.length; ++i) {
            result[i] = result[i].trim();
        }

        return result;
    }

    void publishRow(List<Node> queryChildren, ResultSet rs, IExecutionContext context, String[] publishAs) throws SQLException {

        // Publish each column of this result row to the context.
        ResultSetMetaData md = rs.getMetaData();
        int colCount = md.getColumnCount();

        // TODO: Consider alternative of publishing row as a Map that must be
        // queried instead, e.g. jexl="row[id] == 'Alan'"
        // log.info("PublishAs: " + Arrays.asList(publishAs));
        for(int i = 1; i <= colCount; ++i) {
            if(publishAs.length >= i) {
                context.put(publishAs[i - 1], rs.getObject(i));
            }
        }

        // Run all the children waiting for this query row.
        executeChildren(context);
        // if(queryChildren.size() > 0) {
        // TreeWalker walker = new TreeWalker();
        // for(int i = 1; i <= queryChildren.size(); ++i) {
        // log.debug("Running query child " + i + ": " + queryChildren.get(i));
        // walker.execute(queryChildren.get(i), context);
        // }
        // }

        log.debug("Done Running " + queryChildren.size() + " child actions");
    }

    @Override
    public void validate() {
        List<Node> children = getNode().getChildNodesAsList();

        if(children.size() < 1) {
            throw new ValidationException(this, "Query element missing query text.");
        }

        if(!(children.get(0) instanceof Text)) {
            String msg = "First child of <query> must be the query text,  not: " + children.get(0).getClass().getName();
            throw new ValidationException(this, msg);
        }

        // Verify all subsequent entries are Nodes
        for(int i = 1; i < children.size(); ++i) {
            Object kid = children.get(i);
            if(!(kid instanceof Node)) {
                String msg = "Query node " + getNode().toString()
                        + " may not contain Strings after the first element. Invalid value: " + kid.getClass().getName();
                throw new ValidationException(this, msg);
            }
        }

    }
}
