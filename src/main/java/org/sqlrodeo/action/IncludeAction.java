package org.sqlrodeo.action;

import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sqlrodeo.Action;
import org.sqlrodeo.ExecutionContext;
import org.sqlrodeo.implementation.ValidationException;
import org.sqlrodeo.util.StringUtils;
import org.sqlrodeo.xml.Parser;
import org.w3c.dom.Node;

public final class IncludeAction extends BaseAction {

	Logger log = LoggerFactory.getLogger(IncludeAction.class);

	public IncludeAction(Node node) {
		super(node);
	}

	@Override
	public void execute(ExecutionContext context) throws Exception {

		String condition = getNode().getAttribute("if");
		if (StringUtils.isEmpty(condition)
				|| context.evaluateBoolean(condition)) {

			URL relativeUrl = resolveRelativeUrl(context.substitute(getNode()
					.getAttribute("href")));
			Node rootFromReferencedFile = new Parser().parse(relativeUrl);
			Action action = (Action) rootFromReferencedFile
					.getUserData("action");
			action.execute(context);

		} else {
			log.debug("Not running, because if-condition is false: "
					+ toString());
		}
	}

	@Override
	public void validate() throws ValidationException {
		try {
			URL relativeUrl = resolveRelativeUrl(getNode().getAttribute("href"));
			log.debug("Parsing included URL: " + relativeUrl);
			new Parser().parseAndValidate(relativeUrl);
			log.debug("Done Parsing included URL: " + relativeUrl);
		} catch (Exception e) {
			throw new ValidationException(this, e);
		}
	}
}
