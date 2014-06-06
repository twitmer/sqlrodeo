package sqlrodeo;

import java.sql.Connection;
import java.util.Map;

import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.NotFoundException;

/**
 * The context containing all variables and objects created and used during
 * processing of SqlRodeo XML files. This also provides a number of convenience
 * functions used by the IActions to work with JEXL expressions and publish
 * 'close' actions.
 */
public interface IExecutionContext extends Map<String, Object>, AutoCloseable {

    /**
     * Evaluate the given expression with the JEXL engine.
     * 
     * @param jexlExpression
     *            Expression to evaluate.
     * @return Result provided by JEXL.
     * @throws JexlEvaluationException
     */
    Object evaluate(String jexlExpression) throws JexlEvaluationException;

    /**
     * A convenience wrapper around the other evaluate() method in this class
     * that evaluates the given expression as a boolean.
     * 
     * @param jexlExpression
     *            Expression to evaluate.
     * @return Result provided by JEXL, cast to a boolean.
     * @throws JexlEvaluationException
     */
    boolean evaluateBoolean(String jexlExpression)
	    throws JexlEvaluationException;

    /**
     * Find the default database connection. This only works if there is only
     * one Connection in the context.
     * 
     * @return Existing connection object from context, but only if only 1
     *         connection is found.
     * @throws NotFoundException
     *             If 0, 2, or more Connections exist in the context.
     */
    Connection getDefaultConnection() throws NotFoundException;

    /**
     * Push a close action onto the stack. Close acctions are executed when the
     * context is closed.
     * 
     * @see AutoCloseable
     * @param closeAction
     *            Action to execute when this context is closed.
     */
    void pushCloseAction(Action closeAction);

    /**
     * Substitute variable phrases in the given expression. Example: if the
     * expression is "${user} has ${count} emails", the return may be
     * "Luke has 7 emails", provided "user" and "count" are defined in this
     * context with those values.
     * 
     * @param sourceWithJexlExpressions
     *            String expression containing ${} phrases.
     * @return Expression with all known ${} values replaced with their actual
     *         values.
     * @throws JexlEvaluationException
     */
    String substitute(String sourceWithJexlExpressions)
	    throws JexlEvaluationException;
}
