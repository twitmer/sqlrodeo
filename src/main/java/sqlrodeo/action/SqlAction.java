package sqlrodeo.action;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import sqlrodeo.Action;
import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.network.UrlRetriever;
import sqlrodeo.util.StringUtils;

public final class SqlAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(SqlAction.class);

    public SqlAction(Node node) {
	super(node);
    }

    @Override
    public void execute(ExecutionContext context) {

	if (log.isDebugEnabled()) {
	    log.debug(String.format("execute: node".replaceAll(", ", "=%s, ")
		    + "=%s", toString()));
	}

	boolean substitute = getAttributeAsBoolean("substitute", true);
	String href = getNode().getAttribute("href");
	boolean hrefIsPopulated = !StringUtils.isEmpty(href);
	List<Node> children = getNode().getChildNodesAsList();

	// Case 1: Has 0 children, href is populated
	if (children.size() == 0 && hrefIsPopulated) {
	    try {
		URL sqlUrl = resolveRelativeUrl(href);
		String sqlText = UrlRetriever.retrieveTextForUrl(sqlUrl);
		if (substitute) {
		    sqlText = context.substitute(sqlText);
		}

		log.info("Case 1a: sqlUrl=" + sqlUrl + ", sqlText=" + sqlText);
		executeSqlLiteral(context, sqlText);
	    } catch (RuntimeException e) {
		throw e;
	    } catch (Exception e) {
		throw new ExecutionException(this, e);
	    }
	}

	// Case 2: Has 1+ children, possible mix of #text and <Element> nodes.
	else if (children.size() > 0) {

	    for (int i = 0; i < children.size(); ++i) {

		Node child = children.get(i);

		if (child instanceof Text) {
		    try {
			String sqlText = child.getNodeValue();
			if (substitute) {
			    sqlText = context.substitute(sqlText);
			}

			log.info("Case 1b: sqlText=" + sqlText);
			executeSqlLiteral(context, sqlText);
		    } catch (RuntimeException e) {
			throw e;
		    } catch (Exception e) {
			throw new ExecutionException(this, e);
		    }
		} else if (child instanceof Element) {

		    Action childAction = (Action) child.getUserData("action");
		    try {
			childAction.execute(context);
		    } catch (RuntimeException e) {
			throw e;
		    } catch (Exception e) {
			throw new ExecutionException(childAction, e);
		    }
		}
	    }
	} else {
	    throw new ExecutionException(this, "Don't know how to execute!");
	}
    }

    private void executeSqlLiteral(ExecutionContext context, String sqlText)
	    throws SQLException {
	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "executeSqlLiteral: context, sqlText".replaceAll(", ",
			    "=%s, ") + "=%s", context, sqlText));
	}

	boolean oneStatement = getAttributeAsBoolean("oneStatement", false);
	Connection connection = getConnection(context);
	String[] statements;
	if (oneStatement) {
	    statements = new String[] { sqlText.trim() };
	} else {
	    statements = splitStatements(sqlText);
	}

	for (int i = 0; i < statements.length; ++i) {
	    executeStatement(connection, statements[i]);
	}
    }

    private void executeStatement(Connection connection, String sqlText)
	    throws SQLException {
	try (Statement stmt = connection.createStatement()) {
	    log.info("Executing statement: " + sqlText);
	    stmt.execute(sqlText);
	}
    }

    private String[] splitStatements(String sqlText) {

	List<String> result = new ArrayList<>();

	String[] statements = sqlText.trim().split(";");

	for (int i = 0; i < statements.length; ++i) {
	    String stmt = statements[i].trim();

	    // Don't include empty statements.
	    if (!StringUtils.isEmpty(stmt)) {
		result.add(stmt);
	    }
	}

	return result.toArray(new String[result.size()]);
    }

    @Override
    public void validate() {

	// boolean substitute = getAttributeAsBoolean("substitute", true);
	boolean oneStatement = getAttributeAsBoolean("oneStatement", false);
	String href = getNode().getAttribute("href");
	List<Node> children = getNode().getChildNodesAsList();

	// If this <sql> element has an 'href', no child elements are allowed.
	// Doesn't matter if they're #text nodes or Element
	// nodes.
	if (!StringUtils.isEmpty(href) && children.size() > 0) {
	    throw new ValidationException(this,
		    "Sql node cannot include content if href is present. Href="
			    + href);
	}

	if (oneStatement && children.size() > 0) {
	    throw new ValidationException(this,
		    "oneStatement cannot be true if child nodes are present.");
	}
    }

}
