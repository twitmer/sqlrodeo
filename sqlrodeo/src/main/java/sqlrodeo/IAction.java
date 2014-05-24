package sqlrodeo;

import java.net.URL;

import sqlrodeo.xml.LessStupidNode;

/**
 * Each IAction implements a specific element in the SqlRodeo XML file.
 */
public interface IAction {

	/**
	 * Execute this action against the given context.
	 * 
	 * @param context ISqlRodeoContext to use during execution.
	 * @throws Exception
	 */
	void execute(ISqlRodeoContext context) throws Exception;

	/**
	 * 
	 * @return
	 */
	LessStupidNode getNode();

	boolean isIfConditionTrue(ISqlRodeoContext context);

	/**
	 * Retrieve the line number in the SqlRodeo XML file for this action.
	 * 
	 * @return
	 */
	long resolveLineNumber();

	URL resolveResourceUrl();

	/**
	 * Evaluate whether this action has sufficient and correct information from
	 * the SqlRodeo XML file to perform the intended action.
	 */
	void validate() throws Exception;
}