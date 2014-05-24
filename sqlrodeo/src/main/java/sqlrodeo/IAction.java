package sqlrodeo;

import java.net.URL;

import sqlrodeo.xml.NodeWrapper;

/**
 * Each IAction implements a specific element in the SqlRodeo XML file.
 */
public interface IAction {

	/**
	 * Execute this action against the given context.
	 * 
	 * @param context
	 *            ISqlRodeoContext to use during execution.
	 * @throws Exception
	 */
	void execute(ISqlRodeoContext context) throws Exception;

	/**
	 * Retrieve the XML node for which this action was created.
	 * 
	 * @return Wrapped XML node for this action.
	 */
	NodeWrapper getNode();

	boolean isIfConditionTrue(ISqlRodeoContext context);

	/**
	 * Retrieve the line number in the SqlRodeo XML file for this action.
	 * 
	 * @return Line number in the SqlRodeo XML file that resulted in the
	 *         creation of this action.
	 */
	long resolveLineNumber();

	/**
	 * Retrieve the URL of the SqlRodeo XML file for this action.
	 * 
	 * @return URL of the SqlRodeo XML file that resulted in the creation of
	 *         this action.
	 */
	URL resolveResourceUrl();

	/**
	 * Evaluate whether this action has sufficient and correct information from
	 * the SqlRodeo XML file to perform the intended action.
	 */
	void validate() throws Exception;
}