package beans;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import model.User;


public interface ConnectionManager {

	@POST
	@Path("/register")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public List<String> newConnection(String connection);
	
	@POST
	@Path("/node")
	@Consumes(MediaType.APPLICATION_JSON)
	public void addConnection(String connection);

	@POST
	@Path("/nodes")
	@Produces(MediaType.APPLICATION_JSON)
	public  Collection<String> allConnection();
	
	@POST
	@Path("/users/loggedIn")
	@Consumes(MediaType.APPLICATION_JSON)
	public void allLoggedInUsersPost(HashMap<String,User> users);
	
	@GET
	@Path("/users/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,User> allLoggedInUsers();
	
	@POST
	@Path("/users/registered")
	@Consumes(MediaType.APPLICATION_JSON)
	public void allRegisteredUsersPost(HashMap<String,User> users);
	
	@GET
	@Path("/users/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public HashMap<String,User> allRegisteredUsers();
	
	@DELETE
	@Path("/node/{alias}")
	@Consumes(MediaType.APPLICATION_JSON)
	public  void removeConnection(@PathParam("alias") String alias);
	
	@GET
	@Path("/node")
	@Produces(MediaType.APPLICATION_JSON)
	public  boolean contactConnection();
	
	public String getMaster();

	public void setMaster(String master);

	public String getNodeName();

	public void setNodeName(String nodeName);

	public String getNodeAddr();

	public void setNodeAddr(String nodeAddr);

	public List<String> getConnections();

	public void setConnections(List<String> connections);
}
