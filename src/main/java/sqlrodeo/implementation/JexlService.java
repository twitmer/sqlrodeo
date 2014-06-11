package sqlrodeo.implementation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.ExecutionContext;

final public class JexlService {

    private static final Logger log = LoggerFactory
	    .getLogger(JexlService.class);

    /**
     * Variable is anything beginning with "${", ending with "}". Anything in
     * between "{" and "}" is valid, except for another "{" or "}".
     */
    public static final Pattern VARIABLE_PATTERN = Pattern
	    .compile("\\$\\{[^{}]+\\}");

    private final JexlEngine jexl = new JexlEngine();

    public JexlService() {
    }

    public Object evaluate(String expressionString, ExecutionContext context)
	    throws JexlEvaluationException {
	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "evaluate: expressionString".replaceAll(", ", "=%s, ")
			    + "=%s", expressionString));
	}
	try {
	    // Create engine and context.
	    JexlContext jexlContext = new MapContext(context);

	    // Create and evaluate expression
	    Object result = jexl.createExpression(expressionString).evaluate(
		    jexlContext);

	    if (log.isDebugEnabled()) {
		log.debug(String.format(
			"evaluate: expressionString, result, resultClass"
				.replaceAll(", ", "=%s, ") + "=%s",
			expressionString, result, (result != null ? result
				.getClass().getName() : "null")));
	    }
	    return result;
	} catch (JexlException je) {
	    throw new JexlEvaluationException("Failed to evaluate: "
		    + expressionString, je);
	}
    }

    public Object script(String script, ExecutionContext context)
	    throws JexlEvaluationException {
	if (log.isDebugEnabled()) {
	    log.debug(String.format("script: script".replaceAll(", ", "=%s, ")
		    + "=%s", script));
	}
	try {
	    // Create engine and context.
	    JexlContext jexlContext = new MapContext(context);

	    // Create and evaluate script
	    Object result = jexl.createScript(script).execute(jexlContext);
	    if (log.isDebugEnabled()) {
		log.debug(String.format(
			"script: script, result, resultClass".replaceAll(", ",
				"=%s, ") + "=%s", script, result,
			(result != null ? result.getClass().getName() : "null")));
	    }
	    return result;
	} catch (JexlException je) {
	    throw new JexlEvaluationException("Failed to execute: " + script,
		    je);
	}
    }

    public boolean evaluateBoolean(String expressionString,
	    ExecutionContext context) throws JexlEvaluationException {
	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "evaluateBoolean: expressionString".replaceAll(", ",
			    "=%s, ") + "=%s", expressionString));
	}

	Object result = null;
	result = evaluate(expressionString, context);

	if (result instanceof Boolean) {
	    return (Boolean) result;
	} else {
	    throw new JexlEvaluationException("Expected boolean result from "
		    + expressionString + ", but received: "
		    + result.getClass().getName());
	}
    }

    public String substitute(String source, ExecutionContext context)
	    throws JexlEvaluationException {
	if (log.isDebugEnabled()) {
	    log.debug(String.format(
		    "substitute: source".replaceAll(", ", "=%s, ") + "=%s",
		    source));
	}

	if (source == null) {
	    return source;
	}

	try {
	    JexlContext jexlContext = new MapContext(context);

	    String workingSource = source;
	    Matcher matcher = VARIABLE_PATTERN.matcher(workingSource);
	    while (matcher.find()) {
		String term = matcher.group(0);
		// log.debug("Found: " + term + " in " + workingSource);

		// Remove the "${" and "}" parts of the match so we can evaluate
		// it
		// via JEXL.
		term = term.substring(2, term.length() - 1);

		// Evaluate the term inside the "${" and "}"
		Object result = jexl.createExpression(term).evaluate(
			jexlContext);

		// log.debug("evaluated " + term + " to " + o);
		// Now replace all instances of the variable with its evaluated
		// equivalent. Use 'replace' method to replace text, since it
		// does not expand
		// regexes.
		String newWorkingSource = workingSource.replace("${" + term
			+ "}", result.toString());

		// log.debug("New workingSource = " + workingSource);

		if (newWorkingSource.equals(workingSource)) {
		    throw new JexlEvaluationException("Failed to replace "
			    + term + " with " + result + " in " + workingSource);
		}
		workingSource = newWorkingSource;

		// Replace the matcher with a new one.
		matcher = VARIABLE_PATTERN.matcher(workingSource);
	    }

	    if (log.isDebugEnabled()) {
		log.debug("Produced " + workingSource);
	    }

	    return workingSource;
	} catch (JexlException je) {
	    throw new JexlEvaluationException(je);
	}
    }
}
