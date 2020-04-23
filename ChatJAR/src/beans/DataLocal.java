package beans;

import java.util.HashMap;

import javax.ejb.Local;

import model.User;

@Local
public interface DataLocal {

	public User register(User user);
	public User login(User user);
	public boolean logout(String username);
	
	public HashMap<String, User> getAllUsers();
	public HashMap<String, User> getActiveUsers();
	
}
