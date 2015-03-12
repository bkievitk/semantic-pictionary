package tools;

/**
 * Store user name, password and ID.
 * @author bkievitk
 */

public class UserCredentials {

	public String userName;
	public String password;
	public int userID;
	
	public UserCredentials(String userName, String password, int userID) {
		this.userID = userID;
		this.password = password;
		this.userName = userName;
	}
	
	public String toString() {
		return "{[" + userName + "][" + password + "][" + userID + "]}";
	}
}
