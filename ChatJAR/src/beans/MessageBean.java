package beans;

import java.util.ArrayList;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Message;
import model.User;

@Stateless
@Path("/messages")
@LocalBean
public class MessageBean {
	
	@EJB
	private DataLocal data;

	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageAll(Message message) {
		User sender = data.getActiveUsers().get(message.getSender().getUsername());
		
		if (sender == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("User is not logged in").build();
		}	
		
		if (message.getSender() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
			
		message.setReciver(null);
		
		for (User user: data.getAllUsers().values()) {
			user.getMessages().add(message);
		}
		
		System.out.println(message + " is snet [ALL]");
		
		return Response.status(Response.Status.OK).build();
	}
	
	@POST
	@Path("/user")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageUser(Message message) {
		User sender = data.getActiveUsers().get(message.getSender().getUsername());
		User reciver = data.getAllUsers().get(message.getReciver().getUsername());
		
		if (sender == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("User is not logged in").build();
		}	
		
		if (message.getSender() == null || message.getReciver() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
		
		if (reciver == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid reciver").build();
		}

		if (!sender.getUsername().equals(reciver.getUsername())) {
			data.getAllUsers().get(message.getSender().getUsername()).getMessages().add(message);
		}
		data.getAllUsers().get(message.getReciver().getUsername()).getMessages().add(message);
		
		System.out.println(message + " is snet");
		
		return Response.status(Response.Status.OK).build();
	}
	
	@GET
	@Path("/{username}")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response userMessages(@PathParam("username") String username) {
		User user = data.getAllUsers().get(username);
		
		if (user == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid user").build();
		}
		
		ArrayList<Message> messages = user.getMessages();
		
		System.out.println(user.getUsername() + " messages:");
		for (Message message : messages) {
			System.out.println(message);
		}
		
		
		return Response.status(Response.Status.OK).entity(messages).build();
	}
}
