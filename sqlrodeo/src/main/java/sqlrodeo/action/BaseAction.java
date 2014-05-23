package sqlrodeo.action;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.Connection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import sqlrodeo.IAction;
import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.NotFoundException;
import sqlrodeo.util.StringUtils;
import sqlrodeo.xml.LessStupidNode;

public abstract class BaseAction implements IAction {

    private static final Logger log = LoggerFactory.getLogger(BaseAction.class);

    private final LessStupidNode node;

    public BaseAction(Node node) {
        this.node = new LessStupidNode(node);
    }

    public void executeChildren(ISqlRodeoContext context) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("executeChildren: node".replaceAll(", ", "=%s, ") + "=%s", getNode().toString()));
        }

        // Evaluate possible 'if' condition.
        if(!isIfConditionTrue(context)) {
            log.debug("Not executing: 'if' condition is false: " + getNode().getAttribute("if"));
            return;
        }

        // Run each child of type 'Node'. (Strings are values, typically
        // consumed by their parent.)
        List<Node> kids = getNode().getChildNodesAsList();
        for(int i = 0; i < kids.size(); ++i) {
            LessStupidNode kid = new LessStupidNode(kids.get(i));

            // log.debug("NODE: " + kids.get(i).getClass().getName());
            // com.sun.org.apache.xerces.internal.dom.DeferredElementImpl impl;
            if(kids.get(i) instanceof Element) {
                IAction action = (IAction)kid.getUserData("action");
                log.debug("Action is " + action + " for node: " + kid);
                // TODO: Check for NPE.
                try {
                    if(action.isIfConditionTrue(context)) {
                        log.info("Executing action: " + action.toString() + " in " + action.resolveResourceUrl());
                        action.execute(context);
                    } else {
                        log.info("Skipping action because 'if' condition is false: " + action.toString() + " in "
                                + action.resolveResourceUrl());
                    }
                } catch(RuntimeException e) {
                    throw e;
                } catch(Exception e) {
                    throw new ExecutionException(action.resolveResourceUrl(), action.resolveLineNumber(), kid, e);
                }
            }
        }
    }

    protected boolean getAttributeAsBoolean(String name, boolean defaultValue) {
        String valueAsStr = getNode().getAttribute(name);
        if(!StringUtils.isEmpty(valueAsStr)) {
            return Boolean.parseBoolean(valueAsStr);
        }

        return defaultValue;
    }

    @Override
    public Connection getConnection(ISqlRodeoContext context) {

        LessStupidNode workingNode = getNode();
        String connId = workingNode.getAttribute("connection-id");
        if(log.isDebugEnabled()) {
            log.debug(String.format("getConnection: connId".replaceAll(", ", "=%s, ") + "=%s", connId));
        }

        // If connId is specified, it better exist!
        if(!StringUtils.isEmpty(connId)) {
            Connection conn = (Connection)context.get(connId);
            if(conn == null) {
                throw new ExecutionException(resolveResourceUrl(), resolveLineNumber(), getNode(), "Connection not found: "
                        + connId);
            }
            return conn;
        }

        while(workingNode != null && StringUtils.isEmpty(connId)) {

            if(!StringUtils.isEmpty(connId)) {

                Connection conn = (Connection)context.get(connId);
                if(conn == null) {
                    throw new ExecutionException(resolveResourceUrl(), resolveLineNumber(), getNode(), "Connection not found: "
                            + connId);
                }
                return conn;
            }

            if(workingNode.getParentNode() != null) {
                workingNode = new LessStupidNode(workingNode.getParentNode());
                connId = workingNode.getAttribute("connection-id");
            } else {
                workingNode = null;
            }
        }

        // Didn't find connection-id on parent node, so see if the context knows
        // which it is.
        try {
            return context.getDefaultConnection();
        } catch(NotFoundException e) {
            throw new ExecutionException(resolveResourceUrl(), resolveLineNumber(), getNode(), e);
        }
    }

    public LessStupidNode getNode() {
        return node;
    }

    @Override
    public boolean isIfConditionTrue(ISqlRodeoContext context) {
        String condition = getNode().getAttribute("if");

        if(log.isDebugEnabled()) {
            log.debug(String.format("isIfConditionTrue: condition".replaceAll(", ", "=%s, ") + "=%s", condition));
        }

        if(StringUtils.isEmpty(condition)) {
            return true;
        }

        try {
            return context.evaluateBoolean(condition);
        } catch(JexlEvaluationException e) {
            throw new ExecutionException(resolveResourceUrl(), resolveLineNumber(), getNode(), e);
        }
    }

    public URL resolveRelativeUrl(String href) throws MalformedURLException, URISyntaxException {
        URL resourceUrl = getNode().resolveResourceUrl();
        return resourceUrl.toURI().resolve(".").resolve(href).toURL();
    }

    @Override
    public long resolveLineNumber() {
        return getNode().resolveLineNumber();
    }

    @Override
    public URL resolveResourceUrl() {
        return getNode().resolveResourceUrl();
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + ": " + getNode().toString();
    }
}
