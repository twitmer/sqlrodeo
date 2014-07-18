package sqlrodeo;

import java.net.URL;

import sqlrodeo.implementation.ValidationException;
import sqlrodeo.xml.NodeWrapper;

/**
 * Each Action implements a specific element in the SqlRodeo XML file.
 */
public interface Action {

    /**
     * Execute this action against the given context.
     * 
     * @param context Context to use during execution.
     * @throws Exception Something has gone wrong.
     */
    void execute(ExecutionContext context) throws Exception;

    /**
     * Retrieve the XML node for which this action was created.
     * 
     * @return Wrapped XML node for this action.
     */
    NodeWrapper getNode();

    /**
     * Retrieve the line number in the SqlRodeo XML file for this action.
     * 
     * @return Line number in the SqlRodeo XML file that resulted in the creation of this action.
     */
    long resolveLineNumber();

    /**
     * Retrieve the URL of the SqlRodeo XML file for this action.
     * 
     * @return URL of the SqlRodeo XML file that resulted in the creation of this action.
     */
    URL resolveResourceUrl();

    /**
     * Evaluate whether this action has sufficient and correct information from the SqlRodeo XML file to perform the intended
     * action.
     * @throws ValidationException if Validation has failed.
     */
    void validate() throws ValidationException;
}
