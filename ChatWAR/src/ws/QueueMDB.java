package ws;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.EJB;
import javax.ejb.MessageDriven;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import javax.jms.TextMessage;

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

			if (message.getReciver() == null) {
				for (User user : data.getAllUsers().values()) {
					user.handleMessage(message);
				}
			} else {
				for (User user : data.getAllUsers().values()) {
					if (user.getUsername().equals(message.getReciver().getUsername())
							|| user.getUsername().equals(message.getSender().getUsername())) {
						user.handleMessage(message);
					}
				}
			}
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}

}
