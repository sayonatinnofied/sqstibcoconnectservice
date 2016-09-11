package support;

import javax.jms.JMSException;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.TextMessage;

import com.tibco.tibjms.TibjmsQueueConnectionFactory;

public class TibcoJMS {

	final static String server_url = "tcp://10.242.31.54:7555";
	final static String user_name = "admin";
	final static String password = "password";
	final static String send_queue_name = "smartenginerequestnew";
	final static String receive_queue_name = "smartengineresponsenew";
	final static int retry_count = 3;

	private QueueConnection connection;
	private QueueSession session;
	private Queue receiverQueue;
	private Queue senderQueue;

	private String messageKey;
	private String messageBody;
	

	/**
	 * @return the messageKey
	 */
	public String getMessageKey() {
		return messageKey;
	}

	/**
	 * @param messageKey the messageKey to set
	 */
	public void setMessageKey(String messageKey) {
		this.messageKey = messageKey;
	}

	/**
	 * @return the messageBody
	 */
	public String getMessageBody() {
		return messageBody;
	}

	/**
	 * @param messageBody the messageBody to set
	 */
	public void setMessageBody(String messageBody) {
		this.messageBody = messageBody;
	}

	TibcoJMS() throws JMSException {

		QueueConnectionFactory factory = new TibjmsQueueConnectionFactory(server_url);
		this.connection = factory.createQueueConnection(user_name, password);
		this.session = connection.createQueueSession(false, javax.jms.Session.AUTO_ACKNOWLEDGE);
		// Use createQueue() to enable sending into dynamic queues.
		this.senderQueue = session.createQueue(send_queue_name);
		this.receiverQueue = session.createQueue(receive_queue_name);
	}
	
	TibcoJMS(String messageKey, String messageBody) throws JMSException {
		super();
		this.messageKey = messageKey;
		this.messageBody = messageBody;
	}

	private boolean sendMessage() throws JMSException {
		
		System.out.println("Sending JMS message to server " + server_url + "...");
		QueueSender sender = session.createSender(senderQueue);
		TextMessage jmsMessage = session.createTextMessage();
		
		// Use createQueue() to enable receiving from dynamic queues.
		jmsMessage.setText(messageBody);
		jmsMessage.setJMSCorrelationID(messageKey);
		jmsMessage.setJMSReplyTo(this.receiverQueue);
		sender.send(jmsMessage);
		System.out.println("Sent message: " + messageBody);
		sender.close();
		return true;
		
	}

	private String receiveMessage() throws JMSException {
		String message = "";
		int retryCount = 0;
		QueueReceiver receiver = session.createReceiver(receiverQueue, "JMSCorrelationID='" + messageKey + "'");
		/* read queue messages */
		while (retry_count > retryCount) {
			retryCount++;
			System.out.println("Attempt to receive message: "+retryCount);
			TextMessage jmsMessage = (TextMessage) receiver.receive(5000);
			if (jmsMessage == null)
				continue;
			else {
				message = jmsMessage.getText();
				System.out.println("Received message: " + message);
				break;
			}
		}
		receiver.close();
		return message;
	}
	

	public String getTibcoResponse() throws JMSException {
		String message = "Hi i am tibco Response!!";
		connection.start();
		if(sendMessage()){
			message = receiveMessage();
			connection.stop();
		}
		return message;
	}
	
}
