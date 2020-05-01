package beans;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import model.User;
import util.NodeManager;

@Singleton
@Startup
@Remote(ConnectionManager.class)
@Path("/connection")
public class ConnectionManagerBean implements ConnectionManager {
	
	private String nodeAddr;
	private String nodeName = null;
	private String master = null;
	private List<String> connections = new ArrayList<String>();
	
	@EJB DataLocal data;

	@PostConstruct
	private void init() {
		try {
			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
			this.nodeAddr = (String) mBeanServer.getAttribute(http, "boundAddress");
			this.nodeName = NodeManager.getNodeName() + ":8080";
			
			System.out.println("nodeAddr: " + nodeAddr + "; nodeName: " + nodeName);

			if (master != null && !master.equals("")) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/ChatWAR/connection");
				ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
				this.connections = rest.newConnection(this.nodeName);
				this.connections.remove(this.nodeName);
				this.connections.add(this.master);
				
				List<User> activeUsers = rest.allLoggedInUsers();
				for (User user : activeUsers) {
					this.data.getActiveUsers().put(user.getUsername(), user);
				}
				
				List<User> allUsers = rest.allRegisteredUsers();
				for (User user : allUsers) {
					this.data.getAllUsers().put(user.getUsername(), user);
				}
				
				System.out.println("Connections:");
				for (String string : connections) {
					System.out.println(string);
				}
				
				System.out.println("Registered Users:");
				for (User user : allUsers) {
					System.out.println(user);
				}
				
				System.out.println("LoggedIn Users:");
				for (User user : activeUsers) {
					System.out.println(user);
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<String> newConnection(String connection) {
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		for (String c : connections) {
			ResteasyWebTarget rtarget = client.target("http://" + c + "/ChatWAR/connection");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.addConnection(connection);
		}
		connections.add(connection);
		
		return connections;
	}

	@Override
	public void addConnection(String connection) {
		connections.add(connection);

	}

	@Override
	public List<String> allConnection() {
		
		return this.connections;
	}

	@Override
	public void allLoggedInUsers(List<User> users) {
		for (User user : users) {
			this.data.getActiveUsers().put(user.getUsername(), user);
		}
	}
	
	@Override
	public List<User> allLoggedInUsers() {

		return (List<User>) this.data.getActiveUsers().values();
	}

	@Override
	public void allRegisteredUsers(List<User> users) {

		for (User user : users) {
			this.data.getAllUsers().put(user.getUsername(), user);
		}
	}
	
	@Override
	public List<User> allRegisteredUsers() {
		
		return (List<User>) this.data.getAllUsers().values();
	}

	@Override
	public void removeConnection(String alias) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean contactConnection() {
		// TODO Auto-generated method stub
		return false;
	}

}
