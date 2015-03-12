package tools;

/**
 * A completion listener is called when a model has been finalized and is asking to be transmitted.
 * READY
 * @author bkievitk
 */

public interface CompletionListener {
	public void actionComplete();
	public boolean isComplete();
}
