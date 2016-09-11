package main;

import support.ExceptionHandler;
import support.SqsJms;

public class SQSListener {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
        // TODO code application logic here
        System.setProperty("org.apache.commons.logging.Log",
                           "org.apache.commons.logging.impl.NoOpLog");
		boolean loop = true;
		try {
			SqsJms sqsjms = new SqsJms();
			while(loop) {
				loop = sqsjms.startSQSListen();
			}
		} catch (Exception e) {
			ExceptionHandler.handleException(e);
			System.out.println("Restarting Listener...");
			main(args);
		}

	}

}
