package beans;

import java.util.HashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;

import model.User;

@Singleton
@LocalBean
public class DataBean implements DataLocal{
	
	private HashMap<String, User> allUsers;
	private HashMap<String, User> activeUsers;
	
	public DataBean() {
		allUsers = new HashMap<String, User>();
		activeUsers = new HashMap<String, User>();
	}

	@Override
	public HashMap<String, User> getAllUsers() {
		return allUsers;
	}

	public void setAllUsers(HashMap<String, User> allUsers) {
		this.allUsers = allUsers;
	}

	@Override
	public HashMap<String, User> getActiveUsers() {
		return activeUsers;
	}

	public void setActiveUsers(HashMap<String, User> activeUsers) {
		this.activeUsers = activeUsers;
	}
	
	public boolean isRegistered(String username) {
		return (allUsers.get(username) == null)? false:true;
	}
	
	public boolean isLoggedIn(String username) {
		return (activeUsers.get(username) == null)? false:true;
	}

	@Override
	public User register(User user) {
		String username = user.getUsername();
		if (!isRegistered(username)) {
			allUsers.put(user.getUsername(), user);
			return user;
		}

		return null;
	}

	@Override
	public User login(User user) {
		String username = user.getUsername();
		if (isRegistered(username)) {
			if (!isLoggedIn(username)) {
				if (allUsers.get(username).getPassword().equals(user.getPassword())) {
					activeUsers.put(user.getUsername(), user);
				}else {
					return null;
				}
			}
			return allUsers.get(username);
		}
		return null;
	}

	@Override
	public boolean logout(String username) {
		if (isRegistered(username)) {
			if (isLoggedIn(username)) {
				activeUsers.remove(username);
			}
			
			return true;
		}
		return false;
	}
}
