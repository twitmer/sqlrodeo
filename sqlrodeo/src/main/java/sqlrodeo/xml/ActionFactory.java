package sqlrodeo.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;

import sqlrodeo.Action;
import sqlrodeo.action.AssignAction;
import sqlrodeo.action.CommitAction;
import sqlrodeo.action.ConnectionAction;
import sqlrodeo.action.DelegateAction;
import sqlrodeo.action.ExitAction;
import sqlrodeo.action.IfAction;
import sqlrodeo.action.IncludeAction;
import sqlrodeo.action.ObjectFactoryAction;
import sqlrodeo.action.PropertiesAction;
import sqlrodeo.action.QueryAction;
import sqlrodeo.action.RollbackAction;
import sqlrodeo.action.SqlAction;
import sqlrodeo.action.SqlRodeoAction;
import sqlrodeo.implementation.ValidationException;

final class ActionFactory {

    private static final Logger log = LoggerFactory.getLogger(ActionFactory.class);

    public static Action build(Element element) {

        if(log.isDebugEnabled()) {
            log.debug(String.format("build: element".replaceAll(", ", "=%s, ") + "=%s", element));
        }
        switch(element.getNodeName()) {
            case "objectFactory":
                return new ObjectFactoryAction(element);
            case "if":
                return new IfAction(element);
            case "connection":
                return new ConnectionAction(element);
            case "commit":
                return new CommitAction(element);
            case "rollback":
                return new RollbackAction(element);
            case "properties":
                return new PropertiesAction(element);
            case "exit":
                return new ExitAction(element);
            case "query":
                return new QueryAction(element);
            case "delegate":
                return new DelegateAction(element);
            case "sql":
                return new SqlAction(element);
            case "assign":
                return new AssignAction(element);
            case "include":
                return new IncludeAction(element);
            case "sql-rodeo":
                return new SqlRodeoAction(element);
            default:
                // Shouldn't be possible if XSD validation succeeded.
                NodeWrapper node = new NodeWrapper(element);
                throw new ValidationException(node.resolveResourceUrl(), node.resolveLineNumber(), node,
                        "Could not determine appropriate Action to create.");
        }
    }

    private ActionFactory() {
    }
}
