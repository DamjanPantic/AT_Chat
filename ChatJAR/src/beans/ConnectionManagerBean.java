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
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.ejb.Remote;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.ws.rs.Path;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.User;
import util.NodeManager;
import ws.WSEndPoint;

@Singleton
@Startup
@Path("/connection")
@Remote(ConnectionManager.class)
public class ConnectionManagerBean implements ConnectionManager {

	public static String master = null;
	public static String nodeName = "6e1c441d.ngrok.io";
//	public static String nodeAddr;
	public static List<String> connections = new ArrayList<String>();

	@EJB
	private DataBean data;

	@EJB
	WSEndPoint ws;

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
				connections = rest.newConnection(nodeName);
				connections.remove(nodeName);
				connections.add(master);

				data.setActiveUsers(rest.allLoggedInUsers());

				data.setAllUsers(rest.allRegisteredUsers());

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
	
	@PreDestroy
	private void destroy() {
		ResteasyClient client = new ResteasyClientBuilder().build();
		
		for (String connection : connections) {
			ResteasyWebTarget rtarget = client.target("http://" + connection + "/WAR2020/rest/server");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
			rest.removeConnection(nodeName);
		}
		
		System.out.println("Node is destroyed");
	}

	@Schedule(hour = "*", minute = "*", second = "*/30", info = "heartbeat")
	public void heartBeat() {
		System.out.println("heartbeat");
		
		ResteasyClient client = new ResteasyClientBuilder().build();

		for (String c : connections) {
			ResteasyWebTarget rtarget = client.target("http://" + c + "/WAR2020/rest/server");
			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);

			boolean node = rest.contactConnection();

			if (!node) {
				System.out.println("heartbeat second try");
				node = rest.contactConnection();
				if (!node) {
					connections.remove(c);
					for (String connection : connections) {
						rtarget = client.target("http://" + connection + "/WAR2020/rest/server");
						rest.removeConnection(c);
					}
					System.out.println("Node removed");
				}
			}

		}
		
		System.out.println("Connections: ");
		for (String c : connections) {
			System.out.println(c);
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

		return connections;
	}

	@Override
	public void allLoggedInUsersPost(HashMap<String, User> users) {
		this.data.setActiveUsers(users);

		try {
			ObjectMapper mapper = new ObjectMapper();
			String action = "active:";
			String jsonMessage = mapper.writeValueAsString(data.getActiveUsers().values());
			ws.echoTextMessage(action + jsonMessage);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, User> allLoggedInUsers() {

		return this.data.getActiveUsers();
	}

	@Override
	public void allRegisteredUsersPost(HashMap<String, User> users) {

		this.data.setAllUsers(users);

		try {
			ObjectMapper mapper = new ObjectMapper();
			String action = "all:";
			String jsonMessage = mapper.writeValueAsString(data.getAllUsers().values());
			ws.echoTextMessage(action + jsonMessage);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}
	}

	@Override
	public HashMap<String, User> allRegisteredUsers() {

		return this.data.getAllUsers();
	}

	@Override
	public void removeConnection(String alias) {
		connections.remove(alias);
		
		System.out.println("Connections after remove" + alias + ": ");
		for (String c : connections) {
			System.out.println(c);
		}
	}

	@Override
	public boolean contactConnection() {

		System.out.println("Ping");
		return true;
	}

}
