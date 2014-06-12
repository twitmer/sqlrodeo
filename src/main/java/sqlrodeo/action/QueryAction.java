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

import sqlrodeo.ExecutionContext;
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
    public void execute(ExecutionContext context) throws SQLException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("execute: node".replaceAll(", ", "=%s, ") + "=%s", toString()));
        }
        String queryString = getNodeText();

        String[] publishAs;
        try {
            publishAs = getPublishAs(context.substitute(getNode().getAttribute("publish-as")));
        } catch(JexlEvaluationException e1) {
            throw new ExecutionException(this, e1.getMessage(), e1);
        }

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
                throw new ExecutionException(this, e.getMessage(), e);
            }
        }

        Connection connection = getConnection(context);
        long resultCount = 0;
        long rowCount = 0;
        try (Statement stmt = connection.createStatement()) {
            log.info("Executing statement: " + queryString);
            final String qString = queryString;
            try (ResultSet result = stmt.executeQuery(qString)) {
                while(result.next()) {
                    publishRow(queryChildren, result, context, publishAs);
                    if(!StringUtils.isEmpty(rowNumId)) {
                        context.put(rowNumId, rowCount);
                    }
                    ++resultCount;
                }
            }
        }

        // Optionally publish the number of rows retrieved.
        String rowCountId = getNode().getAttribute("rowcount");
        if(!StringUtils.isEmpty(rowCountId)) {
            context.put(rowCountId, resultCount);
        }
    }

    ResultSet executeQuery(Connection connection, String sqlText) throws SQLException {

        ResultSet result = null;

        try (Statement stmt = connection.createStatement()) {
            log.info("Executing statement: " + sqlText);
            result = stmt.executeQuery(sqlText);
        }
        return result;
    }

    String[] getPublishAs(String publishAs) {

        if(StringUtils.isEmpty(publishAs)) {
            return new String[0];
        }

        // TODO: Split on whitespace and commas.
        String[] result = publishAs.split(",");
        for(int i = 0; i < result.length; ++i) {
            result[i] = result[i].trim();
        }

        return result;
    }

    void publishRow(List<Node> queryChildren, ResultSet rs, ExecutionContext context, String[] publishAs) throws SQLException {

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
