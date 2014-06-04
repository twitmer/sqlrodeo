package sqlrodeo.implementation;

import java.sql.Connection;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.Action;
import sqlrodeo.IExecutionContext;

public final class ExecutionContext implements IExecutionContext {

    /** Actions to implement upon closure. */
    private final Deque<Action> closeActions = new ArrayDeque<>();

    /**
     * Delegate for Map<> implementation.
     */
    private final Map<String, Object> delegateMap = new HashMap<>();

    private final JexlService jexlService = new JexlService();

    /** Logger */
    private final Logger log = LoggerFactory.getLogger(ExecutionContext.class);

    public ExecutionContext() {
        delegateMap.put("sysProps", System.getProperties());
        delegateMap.put("env", System.getenv());
    }

    @Override
    public void clear() {
        delegateMap.clear();
    }

    /**
     * Close this context.
     */
    @Override
    public void close() {
        if(log.isDebugEnabled()) {
            log.debug("close()");
        }

        executeCloseActions();
    }

    @Override
    public boolean containsKey(Object key) {
        return delegateMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return delegateMap.containsValue(value);
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return delegateMap.entrySet();
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj)
            return true;
        if(obj == null)
            return false;
        if(getClass() != obj.getClass())
            return false;
        ExecutionContext other = (ExecutionContext)obj;
        if(closeActions == null) {
            if(other.closeActions != null)
                return false;
        } else if(!closeActions.equals(other.closeActions))
            return false;
        if(delegateMap == null) {
            if(other.delegateMap != null)
                return false;
        } else if(!delegateMap.equals(other.delegateMap))
            return false;
        if(log == null) {
            if(other.log != null)
                return false;
        } else if(!log.equals(other.log))
            return false;
        return true;
    }

    @Override
    public Object evaluate(String jexlExpression) throws JexlEvaluationException {
        return jexlService.evaluate(jexlExpression, this);
    }

    @Override
    public boolean evaluateBoolean(String jexlExpression) throws JexlEvaluationException {
        return jexlService.evaluateBoolean(jexlExpression, this);
    }

    private void executeCloseActions() {
        if(log.isDebugEnabled()) {
            log.debug(String.format("executeCloseActions: closeActions".replaceAll(", ", "=%s, ") + "=%s", closeActions.size()));
        }

        while(closeActions.size() > 0) {
            Action closeAction = closeActions.pop();
            log.debug("Running closeAction: " + closeAction);
            try {
                closeAction.execute(this);
            } catch(Exception e) {
                log.debug("Ignoring error when executing closeAction (" + closeAction + "): " + e.getMessage());
            }
        }
    }

    @Override
    public Object get(Object key) {
        Object value = delegateMap.get(key);
        log.debug("CONTEXT get: " + key + " = " + value + " (" + (value != null ? value.getClass().getName() : "null") + ")");
        return value;
    }

    /**
     * Find the default connection in the context. This only works if there is only one Connection in the context.
     * 
     * @param context
     * @return
     * @throws NotFoundException
     */
    @Override
    public Connection getDefaultConnection() throws NotFoundException {

        List<Connection> connections = new ArrayList<>();
        for(Object value : delegateMap.values()) {
            if(value instanceof Connection) {
                connections.add((Connection)value);
            }
        }

        if(connections.size() == 1) {
            return connections.get(0);
        } else {
            throw new NotFoundException("Could not identify default connecton from collection of " + connections.size()
                    + " connections");
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((closeActions == null) ? 0 : closeActions.hashCode());
        result = prime * result + ((delegateMap == null) ? 0 : delegateMap.hashCode());
        result = prime * result + ((log == null) ? 0 : log.hashCode());
        return result;
    }

    @Override
    public boolean isEmpty() {
        return delegateMap.isEmpty();
    }

    @Override
    public Set<String> keySet() {
        return delegateMap.keySet();
    }

    /**
     * Push a close action onto the stack.
     * 
     * @param closeAction Action to execute when this context is closed.
     */
    @Override
    public void pushCloseAction(Action closeAction) {
        closeActions.push(closeAction);
    }

    @Override
    public Object put(String key, Object value) {
        log.debug("CONTEXT put: " + key + " = " + value + " (" + (value != null ? value.getClass().getName() : "null") + ")");
        return delegateMap.put(key, value);
    }

    @Override
    public void putAll(Map<? extends String, ? extends Object> m) {
        for(Map.Entry<? extends String, ? extends Object> entry : m.entrySet()) {
            put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public Object remove(Object key) {
        return delegateMap.remove(key);
    }

    @Override
    public int size() {
        return delegateMap.size();
    }

    @Override
    public String substitute(String source) throws JexlEvaluationException {
        return jexlService.substitute(source, this);
    }

    @Override
    public Collection<Object> values() {
        return delegateMap.values();
    }
}
