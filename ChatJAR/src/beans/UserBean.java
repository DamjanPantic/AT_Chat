package beans;

import java.util.Collection;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import model.User;
import ws.WSEndPoint;

@Stateless
@Path("/users")
@LocalBean
public class UserBean {

	@EJB
	private DataLocal data;

	@EJB
	WSEndPoint ws;

//	@EJB
//	ConnectionManager connection;

	@POST
	@Path("/register")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response register(User user) {
		User userRegistered = data.register(user);

		if (userRegistered == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong! Username already exists")
					.build();
		}

		System.out.println(user + " is regitered");

//		ResteasyClient client = new ResteasyClientBuilder().build();
//
//		for (String c : ConnectionManagerBean.connections) {
//			ResteasyWebTarget rtarget = client.target("http://" + c + "/ChatWAR/connection");
//			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
//			rest.allRegisteredUsersPost(data.getAllUsers());
//		}

		try {

			ObjectMapper mapper = new ObjectMapper();
			String action = "all:";
			String jsonMessage = mapper.writeValueAsString(data.getAllUsers().values());
			ws.echoTextMessage(action + jsonMessage);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).build();
	}

	@POST
	@Path("/login")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(User user) {
		User userLoggedIn = data.login(user);

		if (userLoggedIn == null) {
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("Something went wrong! Check your username or password").build();
		}

		System.out.println(user + " is logged in");
		
//		ResteasyClient client = new ResteasyClientBuilder().build();
//
//		for (String c : ConnectionManagerBean.connections) {
//			ResteasyWebTarget rtarget = client.target("http://" + c + "/ChatWAR/connection");
//			ConnectionManager rest = rtarget.proxy(ConnectionManager.class);
//			rest.allLoggedInUsersPost(data.getActiveUsers());
//		}

		try {

			ObjectMapper mapper = new ObjectMapper();
			String action = "active:";
			String jsonMessage = mapper.writeValueAsString(data.getActiveUsers().values());
			ws.echoTextMessage(action + jsonMessage);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).entity(userLoggedIn).build();
	}

	@DELETE
	@Path("loggedIn/{username}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response logout(@PathParam("username") String username) {
		boolean userLoggedOut = data.logout(username);

		if (!userLoggedOut) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Something went wrong! User was not found")
					.build();
		}

		System.out.println("User[username: " + username + "] is logged out");

		try {

			ObjectMapper mapper = new ObjectMapper();
			String action = "active:";
			String jsonMessage = mapper.writeValueAsString(data.getActiveUsers().values());
			ws.echoTextMessage(action + jsonMessage);

		} catch (JsonProcessingException e) {

			e.printStackTrace();
		}

		return Response.status(Response.Status.OK).build();
	}

	@GET
	@Path("/loggedIn")
	@Produces(MediaType.APPLICATION_JSON)
	public Response loggedIn() {
		Collection<User> activeUsers = (Collection<User>) data.getActiveUsers().values();

		System.out.println("Active users:");
		for (User user : activeUsers) {
			System.out.println(user);
		}

		return Response.status(Response.Status.OK).entity(activeUsers).build();
	}

	@GET
	@Path("/registered")
	@Produces(MediaType.APPLICATION_JSON)
	public Response registered() {
		Collection<User> allUsers = (Collection<User>) data.getAllUsers().values();

		System.out.println("Registered users:");
		for (User user : allUsers) {
			System.out.println(user);
		}

		return Response.status(Response.Status.OK).entity(allUsers).build();
	}
}
