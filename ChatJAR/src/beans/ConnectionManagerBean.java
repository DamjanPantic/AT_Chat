package beans;

import java.io.File;
import java.io.FileInputStream;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
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
	
	private String master = null;
	private String nodeName = "0ccaf5f9.ngrok.io";
	private String nodeAddr;
	private List<String> connections = new ArrayList<String>();
	
	@EJB
	private DataBean data;

	@PostConstruct
	private void init() {
		try {
//			MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
//			ObjectName http = new ObjectName("jboss.as:socket-binding-group=standard-sockets,socket-binding=http");
//			this.nodeAddr = (String) mBeanServer.getAttribute(http, "boundAddress");
//			this.nodeName = NodeManager.getNodeName() + ":8080";
//			
//			System.out.println("nodeAddr: " + nodeAddr + "; nodeName: " + nodeName);

			if (master != null && !master.equals("")) {
				ResteasyClient client = new ResteasyClientBuilder().build();
				ResteasyWebTarget rtarget = client.target("http://" + master + "/ChatWAR/connection");
				ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
				this.connections = rest.newConnection(this.nodeName);
				this.connections.remove(this.nodeName);
				this.connections.add(this.master);
				
				data.setActiveUsers(rest.allLoggedInUsers());
				for (User user : data.getActiveUsers().values()) {
					this.data.getActiveUsers().put(user.getUsername(), user);
				}
				
				data.setAllUsers(rest.allRegisteredUsers());
				for (User user : data.getAllUsers().values()) {
					this.data.getAllUsers().put(user.getUsername(), user);
				}
				
				System.out.println("Connections:");
				for (String string : connections) {
					System.out.println(string);
				}
				
				System.out.println("Registered Users:");
				for (User user : data.getAllUsers().values()) {
					System.out.println(user);
				}
				
				System.out.println("LoggedIn Users:");
				for (User user : data.getActiveUsers().values()) {
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
	public void allLoggedInUsersPost(HashMap<String,User> users) {
		this.data.setActiveUsers(users);
	}
	
	@Override
	public HashMap<String,User> allLoggedInUsers() {

		return this.data.getActiveUsers();
	}

	@Override
	public void allRegisteredUsersPost(HashMap<String,User> users) {

		this.data.setAllUsers(users);
	}
	
	@Override
	public HashMap<String,User> allRegisteredUsers() {
		
		return this.data.getAllUsers();
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
