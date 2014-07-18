package sqlrodeo;

import java.util.Map;

import sqlrodeo.implementation.JexlEvaluationException;

/**
 * The context containing all variables and objects created and used during
 * processing of SqlRodeo XML files. This also provides a number of convenience
 * functions used by the IActions to work with JEXL expressions and publish
 * 'close' actions. It extends the Map interface to simplify the integration
 * with JEXL.
 */
public interface ExecutionContext extends Map<String, Object>, AutoCloseable {

	/**
	 * Evaluate the given expression with the JEXL engine.
	 * 
	 * @param jexlExpression
	 *            Expression to evaluate.
	 * @return Result provided by JEXL.
	 * @throws JexlEvaluationException
	 *             Error evaluating JEXL expression.
	 */
	Object evaluate(String jexlExpression) throws JexlEvaluationException;

	/**
	 * Evaluate the given script with the JEXL engine.
	 * 
	 * @param scriptText
	 *            Script to evaluate.
	 * @return Result provided by JEXL, which may be void.
	 * @throws JexlEvaluationException
	 *             Error evaluating JEXL script.
	 */
	Object evaluateScript(String scriptText) throws JexlEvaluationException;

	/**
	 * A convenience wrapper around the other evaluate() method in this class
	 * that evaluates the given expression as a boolean.
	 * 
	 * @param jexlExpression
	 *            Expression to evaluate.
	 * @return Result provided by JEXL, cast to a boolean.
	 * @throws JexlEvaluationException
	 *             Error evaluating JEXL expression.
	 */
	boolean evaluateBoolean(String jexlExpression)
			throws JexlEvaluationException;

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
	 *             Error evaluating JEXL expression.
	 */
	String substitute(String sourceWithJexlExpressions)
			throws JexlEvaluationException;
}
