package support;

import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.jms.JMSException;

import com.amazonaws.services.sqs.model.Message;

public class SqsJms {

	final static String message_attribute_name = "messageKey";
	SQS sqs;
	ExecutorService executor;

	public SqsJms() throws JMSException {

		System.out.println("===========================================");
		System.out.println("Getting Started with Amazon SQS");
		System.out.println("===========================================\n");

		sqs = new SQS();
		executor = Executors.newFixedThreadPool(5);

		System.out.println("Listening to receive message...");

	}

	public boolean startSQSListen() throws Exception {
		List<Message> messages = sqs.receiveMessages();
		if (messages.isEmpty()) {
			return true;
		}

		for (Message message : messages) {
			String messageId = message.getMessageId();
			String messageBody = message.getBody();
			String receiptHandle = message.getReceiptHandle();
			String messageKey = message.getMessageAttributes().get(message_attribute_name).getStringValue();

			System.out.println("  Message");
			System.out.println("    MessageId:     " + messageId);
			System.out.println("    ReceiptHandle: " + receiptHandle);
			System.out.println("    MD5OfBody:     " + message.getMD5OfBody());
			System.out.println("    Body:          " + messageBody);
			System.out.println("    MessageKey:    " + messageKey);
			for (Entry<String, String> entry : message.getAttributes().entrySet()) {
				System.out.println("  Attribute");
				System.out.println("    Name:  " + entry.getKey());
				System.out.println("    Value: " + entry.getValue());
			}

			Runnable worker = new SqsJmsThread(message);
			executor.execute(worker);
			executor.shutdown();
			
			System.out.println("Waiting to receive message...");
			return true;
		}

		return false;
	}

}
