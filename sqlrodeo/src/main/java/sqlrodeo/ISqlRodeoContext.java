package sqlrodeo;

import java.sql.Connection;
import java.util.Map;

import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.NotFoundException;

public interface ISqlRodeoContext extends Map<String, Object>, AutoCloseable {

    /**
     * Find the default database connection. This only works if there is only one Connection in the context.
     * 
     * @return
     * @throws NotFoundException
     */
    Connection getDefaultConnection() throws NotFoundException;

    /**
     * Push a close action onto the stack.
     * 
     * @param closeAction Action to execute when this context is closed.
     */
    void pushCloseAction(IAction closeAction);

    Object evaluate(String jexlExpression) throws JexlEvaluationException;

    boolean evaluateBoolean(String jexlExpression) throws JexlEvaluationException;

    String substitute(String sourceWithJexlExpressions) throws JexlEvaluationException;

}
