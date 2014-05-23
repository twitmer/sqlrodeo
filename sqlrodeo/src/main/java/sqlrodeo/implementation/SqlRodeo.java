package sqlrodeo.implementation;

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

import sqlrodeo.IAction;
import sqlrodeo.ISqlRodeo;
import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.SqlRodeoException;
import sqlrodeo.action.ExitException;
import sqlrodeo.xml.Parser;

public final class SqlRodeo implements ISqlRodeo {

    private Logger log = LoggerFactory.getLogger(SqlRodeo.class);

    /*
     * (non-Javadoc)
     * @see sqlrodeo.ISqlRodeo#execute(java.net.URL)
     */
    @Override
    public ISqlRodeoContext execute(URL resourceUrl) {
        return execute(resourceUrl, null);
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.ISqlRodeo#execute(java.net.URL, java.util.Map)
     */
    @Override
    public ISqlRodeoContext execute(URL resourceUrl, Map<String, Object> contextSeed) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("execute: resourceUrl, contextSeed".replaceAll(", ", "=%s, ") + "=%s", resourceUrl, contextSeed));
        }

        try (ISqlRodeoContext context = new SqlRodeoContext()) {

            // Validate and parse all XML, generating actions as we go.
            Node root = loadAndValidate(resourceUrl);

            // Populate the initial rodeocontext
            if(null != contextSeed) {
                context.putAll(contextSeed);
            }

            // Execute the action tree.
            IAction action = (IAction)root.getUserData("action");
            try {
                action.execute(context);
            } catch(ExitException e) {
                log.info("Halting execution due to ExitException");
            } catch(SqlRodeoException e) {
                throw e;
            }
            return context;
        } catch(Exception e) {
            throw new SqlRodeoException("Failed to execute document at " + resourceUrl, e);
        }
    }

    private Node loadAndValidate(URL resourceUrl) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("loadAndValidate: resourceUrl".replaceAll(", ", "=%s, ") + "=%s", resourceUrl));
        }

        try {
            return new Parser().parse(resourceUrl);
        } catch(SqlRodeoException e) {
            throw e;
        } catch(DOMException | SAXException | IOException | ParserConfigurationException | URISyntaxException e) {
            throw new SqlRodeoException("Failed to validate document at " + resourceUrl, e);
        }
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.ISqlRodeo#validate(java.net.URL)
     */
    @Override
    public void validate(URL resourceUrl) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("validate: resourceUrl".replaceAll(", ", "=%s, ") + "=%s", resourceUrl));
        }
        loadAndValidate(resourceUrl);
    }
}
