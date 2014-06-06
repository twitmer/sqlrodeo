package sqlrodeo;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import sqlrodeo.action.ExitException;
import sqlrodeo.implementation.ExecutionContext;
import sqlrodeo.xml.Parser;

/**
 * Main entrance to the SqlRodeo library. Implementations of this class can
 * validate and execute SqlRodeo XML documents.
 */

public final class SqlRodeo  {

    private Logger log = LoggerFactory.getLogger(SqlRodeo.class);

	/**
	 * Execute the XML document at the given URL, including any child documents
	 * referenced via include actions.
	 * 
	 * @param resourceUrl
	 *            URL referencing XML document to execute.
	 * @return context after processing is complete.
	 * @throws SqlRodeoException
	 *             Any error.
	 */
    public IExecutionContext execute(URL resourceUrl) {
        return execute(resourceUrl, null);
    }

	/**
	 * Execute the XML document at the given URL, including any child documents
	 * referenced via include actions.
	 * 
	 * @param resourceUrl
	 *            URL referencing XML document to execute.
	 * @param contextSeed
	 *            Map of items to add to the context before executing the given
	 *            XML document.
	 * @return context after processing is complete.
	 * @throws SqlRodeoException
	 *             Any error.
	 */
    public IExecutionContext execute(URL resourceUrl, Map<String, Object> contextSeed) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("execute: resourceUrl, contextSeed".replaceAll(", ", "=%s, ") + "=%s", resourceUrl, contextSeed));
        }

        try (IExecutionContext context = new ExecutionContext()) {

            // Validate and parse all XML, generating actions as we go.
            Node root = loadAndValidate(resourceUrl);

            log.info("Done parsing");
            
            // Populate the initial rodeocontext
            if(null != contextSeed) {
                context.putAll(contextSeed);
            }

            // Execute the action tree.
            Action action = (Action)root.getUserData("action");
            log.info("Executing Action: " + action);
            try {
                action.execute(context);
            } catch(ExitException e) {
                log.info("Halting execution due to ExitException");
            } catch(SqlRodeoException e) {
                throw e;
            }
            return context;
        } catch(Exception e) {
            throw new SqlRodeoException("Failed to execute document at " + resourceUrl + " because: " + e.getMessage(), e);
        }
    }

    private Node loadAndValidate(URL resourceUrl) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("loadAndValidate: resourceUrl".replaceAll(", ", "=%s, ") + "=%s", resourceUrl));
        }

        try {
            return new Parser().parseAndValidate(resourceUrl);
        } catch(SqlRodeoException e) {
            throw e;
        } catch(DOMException | SAXException | IOException | ParserConfigurationException | URISyntaxException e) {
            throw new SqlRodeoException("Failed to validate document at " + resourceUrl, e);
        }
    }

	/**
	 * Validate the XML document at the given URL, including any child documents
	 * referenced via include actions.
	 * 
	 * @param resourceUrl
	 *            URL referencing XML document to validate.
	 * @throws SqlRodeoException
	 *             Any error.
	 */
    public void validate(URL resourceUrl) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("validate: resourceUrl".replaceAll(", ", "=%s, ") + "=%s", resourceUrl));
        }
        loadAndValidate(resourceUrl);
    }
}
