package model;

import java.util.ArrayList;

import javax.ejb.Remote;
import javax.ejb.Stateful;

@Stateful
@Remote(Agent.class)
public class User implements Agent{

	private String username;
	private String password;
	private Host host;
	
	private ArrayList<Message> messages = new ArrayList<Message>();
	
	public User() {
		super();
		this.messages = new ArrayList<Message>();
	}

	public User(String username, String password, Host host, ArrayList<Message> messages) {
		super();
		this.username = username;
		this.password = password;
		this.host = host;
		this.messages = messages;
	}
	
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Host getHost() {
		return host;
	}

	public void setHost(Host host) {
		this.host = host;
	}

	public ArrayList<Message> getMessages() {
		return messages;
	}

	public void setMessages(ArrayList<Message> messages) {
		this.messages = messages;
	}
	
	@Override
	public String toString() {
		return "User[username: " + username + "]";
	}

	@Override
	public void handleMessage(Message message) {
		this.messages.add(message);
	}
}
