package sqlrodeo;

/**
 * Interface for user-supplied objects to execute at runtime.
 */
public interface IDelegate {

	/**
	 * Execute user-defined action.
	 * 
	 * @param context
	 *            The current context for execution.
	 * @param text
	 *            Optional text contained in <delegate> element in XML document.
	 */
	void execute(IExecutionContext context, String text);

}
