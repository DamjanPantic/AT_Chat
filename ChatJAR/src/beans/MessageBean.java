package beans;

import java.util.ArrayList;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.jms.ConnectionFactory;
import javax.jms.ObjectMessage;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import model.Message;
import model.User;
import ws.WSEndPoint;

@Stateless
@Path("/messages")
@LocalBean
public class MessageBean {
	
	@EJB
	private DataLocal data;
	
	@EJB
	WSEndPoint ws;
	
	@Resource(mappedName = "java:/ConnectionFactory")
	private ConnectionFactory connectionFactory;
	@Resource(mappedName = "java:jboss/exported/jms/queue/mojQueue")
	private Queue queue;

	@POST
	@Path("/all")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response sendMessageAll(Message message){
		User sender = data.getActiveUsers().get(message.getSender().getUsername());
		
		if (sender == null){
			return Response.status(Response.Status.BAD_REQUEST).entity("User is not logged in").build();
		}	
		
		if (message.getSender() == null) {
			return Response.status(Response.Status.BAD_REQUEST).entity("Invalid message").build();
		}
			
		message.setReciver(null);
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender msgSender = session.createSender(queue);
			
			ObjectMessage msg = session.createObjectMessage();
			msg.setObject(message);
			msgSender.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
//		for (User user: data.getAllUsers().values()) {
//			user.getMessages().add(message);
//		}
		
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
		
		try {
			QueueConnection connection = (QueueConnection) connectionFactory.createConnection("guest", "guest.guest.1");
			QueueSession session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			QueueSender msgSender = session.createSender(queue);
			
			ObjectMessage msg = session.createObjectMessage();
			msg.setObject(message);
			msgSender.send(msg);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

//		if (!sender.getUsername().equals(reciver.getUsername())) {
//			data.getAllUsers().get(message.getSender().getUsername()).getMessages().add(message);
//		}
//		data.getAllUsers().get(message.getReciver().getUsername()).getMessages().add(message);
		
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
