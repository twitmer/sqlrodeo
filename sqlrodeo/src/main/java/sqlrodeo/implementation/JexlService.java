package sqlrodeo.implementation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.jexl2.JexlContext;
import org.apache.commons.jexl2.JexlEngine;
import org.apache.commons.jexl2.JexlException;
import org.apache.commons.jexl2.MapContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import sqlrodeo.ISqlRodeoContext;

final class JexlService {

    private static final Logger log = LoggerFactory.getLogger(JexlService.class);

    private final JexlEngine jexl = new JexlEngine();

    /**
     * Variable is anything beginning with "${", ending with "}". Anything in between "{" and "}" is valid, except for another "{"
     * or "}".
     */
    public static final Pattern VARIABLE_PATTERN = Pattern.compile("\\$\\{[^{}]+\\}");

    public JexlService() {
    }

    public Object evaluate(String expressionString, ISqlRodeoContext context) throws JexlEvaluationException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("evaluate: expressionString".replaceAll(", ", "=%s, ") + "=%s", expressionString));
        }
        try {
            // Create engine and context.
            JexlContext jc = new MapContext(context);

            // Create and evaluate expression
            Object o = jexl.createExpression(expressionString).evaluate(jc);

            if(log.isDebugEnabled()) {
                log.debug(String.format("evaluate: expressionString, result, resultClass".replaceAll(", ", "=%s, ") + "=%s",
                        expressionString, o, o.getClass().getName()));
            }
            return o;
        } catch(JexlException je) {
            throw new JexlEvaluationException(je);
        }
    }

    public boolean evaluateBoolean(String expressionString, ISqlRodeoContext context) throws JexlEvaluationException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("evaluateBoolean: expressionString".replaceAll(", ", "=%s, ") + "=%s", expressionString));
        }

        Object result = null;
        result = evaluate(expressionString, context);

        if(result instanceof Boolean) {
            return (Boolean)result;
        } else {
            throw new JexlEvaluationException("Expected boolean result from " + expressionString + ", but received: "
                    + result.getClass().getName());
        }
    }

    public String substitute(String source, ISqlRodeoContext context) throws JexlEvaluationException {
        if(log.isDebugEnabled()) {
            log.debug(String.format("substitute: source".replaceAll(", ", "=%s, ") + "=%s", source));
        }
        try {
            JexlContext jc = new MapContext(context);

            String workingSource = source;
            Matcher matcher = VARIABLE_PATTERN.matcher(workingSource);
            while(matcher.find()) {
                String term = matcher.group(0);
                // log.debug("Found: " + term + " in " + workingSource);

                // Remove the "${" and "}" parts of the match so we can evaluate it
                // via JEXL.
                term = term.substring(2, term.length() - 1);

                // Evaluate the term inside the "${" and "}"
                Object o = jexl.createExpression(term).evaluate(jc);

                // log.debug("evaluated " + term + " to " + o);
                // Now replace all instances of the variable with its evaluated
                // equivalent. Use 'replace' method to replace text, since it does not expand
                // regexes.
                String newWorkingSource = workingSource.replace("${" + term + "}", o.toString());

                // log.debug("New workingSource = " + workingSource);

                if(newWorkingSource.equals(workingSource)) {
                    throw new JexlEvaluationException("Failed to replace " + term + " with " + o + " in " + workingSource);
                }
                workingSource = newWorkingSource;

                // Replace the matcher with a new one.
                matcher = VARIABLE_PATTERN.matcher(workingSource);
            }

            if(log.isDebugEnabled()) {
                log.debug("Converted " + source + " to " + workingSource);
            }

            return workingSource;
        } catch(JexlException je) {
            throw new JexlEvaluationException(je);
        }
    }
}
