package support;

import javax.jms.JMSException;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;

public class ExceptionHandler {

	public static void handleException(Exception e) {
		Throwable cause = e.getCause();
		if (cause instanceof AmazonServiceException) {
			AmazonServiceException ase = (AmazonServiceException) e;
			System.out.println("Caught an AmazonServiceException, which means your request made it "
					+ "to Amazon SQS, but was rejected with an error response for some reason.");
			System.out.println("Error Message:    " + ase.getMessage());
			System.out.println("HTTP Status Code: " + ase.getStatusCode());
			System.out.println("AWS Error Code:   " + ase.getErrorCode());
			System.out.println("Error Type:       " + ase.getErrorType());
			System.out.println("Request ID:       " + ase.getRequestId());
		} else if (cause instanceof AmazonClientException) {
			AmazonClientException ace = (AmazonClientException) e;
			System.out.println("Caught an AmazonClientException, which means the client encountered "
					+ "a serious internal problem while trying to communicate with SQS, such as not "
					+ "being able to access the network.");
			System.out.println("Error Message: " + ace.getMessage());
		} else if(cause instanceof JMSException){
			JMSException je = (JMSException) e;
			System.out.println("Error Message: " + je.getMessage());
			System.out.println("Error Stack: ");
			je.printStackTrace();
		} else {
			System.out.println("Error Message: " + e.getMessage());
			System.out.println("Error Stack: ");
			e.printStackTrace();
		}
	}

}
