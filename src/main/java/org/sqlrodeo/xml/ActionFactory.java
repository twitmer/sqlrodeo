package org.sqlrodeo.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.Action;
import org.sqlrodeo.action.AssignAction;
import org.sqlrodeo.action.CommitAction;
import org.sqlrodeo.action.ConnectionAction;
import org.sqlrodeo.action.ExitAction;
import org.sqlrodeo.action.IfAction;
import org.sqlrodeo.action.IncludeAction;
import org.sqlrodeo.action.ObjectFactoryAction;
import org.sqlrodeo.action.PropertiesAction;
import org.sqlrodeo.action.QueryAction;
import org.sqlrodeo.action.RollbackAction;
import org.sqlrodeo.action.ScriptAction;
import org.sqlrodeo.action.SqlAction;
import org.sqlrodeo.action.SqlRodeoAction;
import org.sqlrodeo.implementation.ValidationException;
import org.w3c.dom.Element;

final class ActionFactory {

	private static final Logger log = LoggerFactory
			.getLogger(ActionFactory.class);

	public static Action build(Element element) {

		if (log.isDebugEnabled()) {
			log.debug(String.format("build: element".replaceAll(", ", "=%s, ")
					+ "=%s", element));
		}

		switch (element.getNodeName()) {
		case "assign":
			return new AssignAction(element);
		case "commit":
			return new CommitAction(element);
		case "connection":
			return new ConnectionAction(element);
		case "exit":
			return new ExitAction(element);
		case "if":
			return new IfAction(element);
		case "include":
			return new IncludeAction(element);
		case "objectFactory":
			return new ObjectFactoryAction(element);
		case "properties":
			return new PropertiesAction(element);
		case "query":
			return new QueryAction(element);
		case "rollback":
			return new RollbackAction(element);
		case "sql-rodeo":
			return new SqlRodeoAction(element);
		case "script":
			return new ScriptAction(element);
		case "sql":
			return new SqlAction(element);
		default:
			// Shouldn't be possible if XSD validation succeeded.
			NodeWrapper node = new NodeWrapper(element);
			throw new ValidationException(node.resolveResourceUrl(),
					node.resolveLineNumber(), node,
					"Could not determine appropriate Action to create.");
		}
	}

	private ActionFactory() {
	}
}
