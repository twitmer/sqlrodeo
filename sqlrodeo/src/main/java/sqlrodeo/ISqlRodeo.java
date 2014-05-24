package sqlrodeo;

import java.net.URL;
import java.util.Map;

/**
 * Main entrance to the SqlRodeo library. Implementations of this class can
 * validate and execute SqlRodeo XML documents.
 */
public interface ISqlRodeo {

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
	IExecutionContext execute(URL resourceUrl) throws SqlRodeoException;

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
	IExecutionContext execute(URL resourceUrl, Map<String, Object> contextSeed)
			throws SqlRodeoException;

	/**
	 * Validate the XML document at the given URL, including any child documents
	 * referenced via include actions.
	 * 
	 * @param resourceUrl
	 *            URL referencing XML document to validate.
	 * @throws SqlRodeoException
	 *             Any error.
	 */
	void validate(URL resourceUrl) throws SqlRodeoException;
}
