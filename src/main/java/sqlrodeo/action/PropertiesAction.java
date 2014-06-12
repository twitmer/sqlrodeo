package sqlrodeo.action;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.ExecutionContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.network.UrlRetriever;
import sqlrodeo.util.StringUtils;

/**
 * Action that implements the <properties> element. This action loads a Properties object from an HREF or embedded text, and
 * propagates the contained values to the execution context.
 */
public final class PropertiesAction extends BaseAction {

    /** Logger */
    Logger log = LoggerFactory.getLogger(PropertiesAction.class);

    /**
     * Constructor.
     * 
     * @param node XML Node for which this action exists.
     */
    public PropertiesAction(Node node) {
        super(node);
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#execute(sqlrodeo.ExecutionContext)
     */
    @Override
    public void execute(ExecutionContext context) {
        if(log.isDebugEnabled()) {
            log.debug(String.format("execute: context".replaceAll(", ", "=%s, ") + "=%s", context));
        }

        try {
            // Get the contents of either the HREF or the embedded text.
            String href = context.substitute(getNode().getAttribute("href"));
            String text = "";
            if(!StringUtils.isEmpty(href)) {
                URL relUrl = resolveRelativeUrl(href);
                text = context.substitute(UrlRetriever.retrieveTextForUrl(relUrl));
            } else {
                text = context.substitute(getNodeText());
            }

            // Load the contents into a Properties object.
            Properties props = new Properties();
            props.load(new StringReader(text));

            // Propagate all contents of the Properties object into the context.
            for(String key : props.stringPropertyNames()) {
                context.put(key, props.getProperty(key));
            }

        } catch(IOException | IllegalArgumentException | JexlEvaluationException | URISyntaxException ex) {
            throw new ExecutionException(this, ex.getMessage(), ex);
        }
    }

    /*
     * (non-Javadoc)
     * @see sqlrodeo.Action#validate()
     */
    @Override
    public void validate() {

        if(log.isDebugEnabled()) {
            log.debug("validate()");
        }

        // If href is specified, children are not allowed.
        if(!StringUtils.isEmpty(getNode().getAttribute("href")) && getNode().getChildNodesAsList().size() > 0) {
            throw new ValidationException(this, "Properties cannot have content and an href.");
        }
    }
}
