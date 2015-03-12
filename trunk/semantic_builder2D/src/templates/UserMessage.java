package templates;

public interface UserMessage {
	public static final int ERROR = 1;
	public static final int WARN = 2;
	public static final int INFORM = 3;
	public abstract void showMessage(String message, int level);
}
