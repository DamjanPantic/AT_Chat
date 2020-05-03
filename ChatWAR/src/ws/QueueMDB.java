package ws;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import beans.ConnectionManagerBean;
import beans.DataBean;
import beans.DataLocal;
import model.User;

@MessageDriven(activationConfig = {
		@ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Queue"),
		@ActivationConfigProperty(propertyName = "destination", propertyValue = "jms/queue/mojQueue") })
public class QueueMDB implements MessageListener {
	@EJB
	WSEndPoint ws;

	@EJB
	private DataLocal data;

	@Override
	public void onMessage(Message msg) {
//		TextMessage tmsg = (TextMessage)msg;
//		try {
//			System.out.println("MDB: " + tmsg.getText());
//			ws.echoTextMessage(tmsg.getText());
//		} catch (JMSException e) {
//			e.printStackTrace();
//		}

		try {
			ObjectMessage objmsg = (ObjectMessage) msg;
			model.Message message = (model.Message) objmsg.getObject();

			ResteasyClient client = new ResteasyClientBuilder().build();

			if (message.getReciver() == null) {
				for (User user : data.getActiveUsers().values()) {
					if (user.getHost().getAddress().equals(ConnectionManagerBean.nodeName)) {
						System.out.println("Polsato svima isti Host");
						user.handleMessage(message);
					} else {
						String path = "http://" + user.getHost().getAddress() + "/ChatWAR/messages/user";
						ResteasyWebTarget rtarget = client.target(path);
						Response response = rtarget.request(MediaType.APPLICATION_JSON)
								.post(Entity.entity(message, MediaType.APPLICATION_JSON));
						System.out.println("Polsato svima razliciti Host");
					}
				}
			} else {
				for (User user : data.getActiveUsers().values()) {
					if (user.getUsername().equals(message.getSender().getUsername())) {
						System.out.println("Polsato Senderu");
						user.handleMessage(message);
					} else if (user.getUsername().equals(message.getReciver().getUsername())) {
						if (user.getHost().getAddress().equals(ConnectionManagerBean.nodeName)) {
							System.out.println("Polsato Reciveru na isti Host");
							user.handleMessage(message);
						} else {
							String path = "http://" + user.getHost().getAddress() + "/ChatWAR/messages/user";
							ResteasyWebTarget rtarget = client.target(path);
							Response response = rtarget.request(MediaType.APPLICATION_JSON)
									.post(Entity.entity(message, MediaType.APPLICATION_JSON));
							System.out.println("Polsato Reciveru na razliciti Host");
						}
					}
				}
			}
//			ObjectMessage objmsg = (ObjectMessage) msg;
//			model.Message message = (model.Message) objmsg.getObject();
//
//			if (message.getReciver() == null) {
//				for (User user : data.getActiveUsers().values()) {
//					user.handleMessage(message);
//				}
//			} else {
//				for (User user : data.getActiveUsers().values()) {
//					if (user.getUsername().equals(message.getReciver().getUsername())
//							|| user.getUsername().equals(message.getSender().getUsername())) {
//						user.handleMessage(message);
//					}
//				}
//			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
