package sqlrodeo.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import sqlrodeo.IDelegate;
import sqlrodeo.ISqlRodeoContext;
import sqlrodeo.implementation.ExecutionException;
import sqlrodeo.implementation.ValidationException;
import sqlrodeo.util.StringUtils;

public final class DelegateAction extends BaseAction {

    Logger log = LoggerFactory.getLogger(DelegateAction.class);

    public DelegateAction(Node node) {
        super(node);
    }

    @Override
    public void execute(ISqlRodeoContext context) throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        // Text is an optional text node.
        String nodeText = null;
        Node childNode = getNode().getFirstChild();
        if(childNode != null) {
            nodeText = childNode.getNodeValue();
        }
        String id = getNode().getAttribute("id");
        String delegateClass = getNode().getAttribute("delegate-class");

        IDelegate delegate = null;

        // First try to find an existing instance of the delegate in the
        // context.
        if(!StringUtils.isEmpty(id)) {
            delegate = (IDelegate)context.get(id);
        }

        // If the delegate wasn't found in the context, we'll need to create it.
        if(delegate == null && !StringUtils.isEmpty(delegateClass)) {

            // println "Using thread context classloader"
            delegate = (IDelegate)Class.forName(delegateClass, true, Thread.currentThread().getContextClassLoader()).newInstance();

            // delegate = (IDelegate)Class.forName(delegateClass).newInstance()

            // Delegate created. If an 'id' is specified, publish to context to
            // allow for future use.
            if(!StringUtils.isEmpty(id)) {
                context.put(id, delegate);
            }
        }

        // Out of strategies. No idea how to create your delegate!
        if(delegate == null) {
            throw new ExecutionException(resolveResourceUrl(), resolveLineNumber(), getNode(),
                    "Could not find or create delegate: " + toString());
        }

        delegate.execute(context, nodeText);
    }

    @Override
    public void validate() {
        String id = getNode().getAttribute("id");
        String delegateClass = getNode().getAttribute("delegate-class");

        if(StringUtils.isEmpty(id) && StringUtils.isEmpty(delegateClass)) {
            throw new ValidationException(resolveResourceUrl(), resolveLineNumber(), getNode(),
                    "At least one of 'id' or 'delegate-class' is required: " + toString());
        }
    }
}
