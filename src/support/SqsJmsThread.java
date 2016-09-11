package support;

import javax.jms.JMSException;

import com.amazonaws.services.sqs.model.Message;

public class SqsJmsThread implements Runnable{
	
	final static String message_attribute_name = "messageKey";
	
	private SQS sqs;
	private TibcoJMS jms;
	private Message message;
	
	SqsJmsThread(Message message) throws JMSException{
		sqs = new SQS();
		jms = new TibcoJMS();
		this.message = message;
	}

	@Override
	public void run() {
    	String messageBody = message.getBody();
    	String receiptHandle = message.getReceiptHandle();
    	String messageKey = message.getMessageAttributes().get(message_attribute_name).getStringValue();
    	
    	try {
		  messageBody = jms.getTibcoResponse();
          
          if(messageBody.isEmpty()){
          	System.out.println("No reponse received from TIBCO.");
	            sqs.deleteMessage(receiptHandle);
          } else {
	            sqs.deleteMessage(receiptHandle);
	            sqs.sendMessage(messageKey, messageBody);
          } 
    	} catch(Exception e){
    		ExceptionHandler.handleException(e);
    		System.out.println("Thread stopped by exception.");
    	}
	}
	

}
