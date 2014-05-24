package sqlrodeo.action;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import sqlrodeo.IAction;
import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.xml.Parser;

public final class IncludeAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(IncludeAction.class);

    public IncludeAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ISqlRodeoContext context) throws Exception {

        if(this.isIfConditionTrue(context)) {
            log.debug("execute(): " + toString());
            // executeChildren(context);

            URL relativeUrl = resolveRelativeUrl(getNode().getAttribute("href"));

            log.debug("Parsing included URL: " + relativeUrl);
            Node subRoot = new Parser().parse(relativeUrl);
            log.debug("Done Parsing included URL: " + relativeUrl);

            log.debug("Executing included URL: " + relativeUrl);
            IAction action = (IAction)subRoot.getUserData("action");
            // TODO: Check for NPE.
            action.execute(context);
        } else {
            log.debug("Not running, because if-condition is false: " + toString());
        }
    }

    @Override
    public void validate() throws URISyntaxException, DOMException, SAXException, IOException, ParserConfigurationException {
        URL relativeUrl = resolveRelativeUrl(getNode().getAttribute("href"));
        log.debug("Parsing included URL: " + relativeUrl);
        try {
            new Parser().parse(relativeUrl);
        } catch(Exception e) {
            throw new ValidationException(this, e);
        }
        log.debug("Done Parsing included URL: " + relativeUrl);
    }
}
