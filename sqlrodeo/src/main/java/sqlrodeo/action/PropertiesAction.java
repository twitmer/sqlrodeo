package sqlrodeo.action;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import org.w3c.dom.Text;

import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.JexlEvaluationException;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.util.StringUtils;
import sqlrodeo.util.UrlRetriever;

public final class PropertiesAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(PropertiesAction.class);

    public PropertiesAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ISqlRodeoContext context) {

        String href = getNode().getAttribute("href");
        List<Node> children = getNode().getChildNodesAsList();

        Properties props = new Properties();
        String text = "";
        try {
            if(!StringUtils.isEmpty(href)) {
                URL relUrl = resolveRelativeUrl(href);
                text = UrlRetriever.retrieveTextForUrl(relUrl);
            }

            else {
                // We should have a text node child.
                if(children.size() == 1 && children.get(0) instanceof Text) {
                    text = children.get(0).getNodeValue();
                }
            }

            text = context.substitute(text);
            props.load(new StringReader(text));

            for(String key : props.stringPropertyNames()) {
                context.put(key, props.getProperty(key));
            }
        } catch(IOException | IllegalArgumentException | JexlEvaluationException | URISyntaxException ex) {
            throw new ExecutionException(this, ex);

        }
    }

    /**
     * Default implementation of validate(). Does nothing.
     */
    @Override
    public void validate() {
        // If href is specified, children are not allowed.
        String href = getNode().getAttribute("href");
        List<Node> children = getNode().getChildNodesAsList();

        if(!StringUtils.isEmpty(href) && children.size() > 0) {
            throw new ValidationException(this,
                    "Properties cannot have content and an href.");
        }
    }
}
