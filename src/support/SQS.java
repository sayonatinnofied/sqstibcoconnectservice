
package support;
/*
 * Copyright 2010-2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.AmazonClientException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.DeleteMessageRequest;
import com.amazonaws.services.sqs.model.Message;
import com.amazonaws.services.sqs.model.MessageAttributeValue;
import com.amazonaws.services.sqs.model.ReceiveMessageRequest;
import com.amazonaws.services.sqs.model.SendMessageRequest;

/**
 * This sample demonstrates how to make basic requests to Amazon SQS using the
 * AWS SDK for Java.
 * <p>
 * <b>Prerequisites:</b> You must have a valid Amazon Web
 * Services developer account, and be signed up to use Amazon SQS. For more
 * information on Amazon SQS, see http://aws.amazon.com/sqs.
 * <p>
 * Fill in your AWS access credentials in the provided credentials file
 * template, and be sure to move the file to the default location
 * (C:\\Users\\Sayon\\.aws\\credentials) where the sample code will load the credentials from.
 * <p>
 * <b>WARNING:</b> To avoid accidental leakage of your credentials, DO NOT keep
 * the credentials file in your source directory.
 */
public class SQS {
	
	final static String key_path = "./rootkey.csv";
	final static String receive_queue_url = "https://sqs.us-west-2.amazonaws.com/692480644307/smartenginerequestnew";
	final static  String send_queue_url = "https://sqs.us-west-2.amazonaws.com/692480644307/smartengineresponsenew";
	final static String message_attribute_name = "messageKey";
	final static String attribute_name = "All";
	final static int retry_count = 3;
	final static int retry_interval = 1000;
	final static int visibility_timeout = 10;
	final static int wait_time_seconds = 1;
	final static int max_no_messages = 1;
	final static int delay_time = 0;
	
	final static String proxy_host = "proxy.cognizant.com";
	final static int proxy_port = 6050;
	final static String proxy_username = "485867";
	final static String proxy_password = "Babun@2709";
	
	AWSCredentials credentials = null; 
	AmazonSQS sqs;
	
	SQS(){
    	
        try {
            credentials = new ProfileCredentialsProvider(key_path, "default").getCredentials();
        } catch (Exception e) {
            throw new AmazonClientException(
                    "Cannot load the credentials from the credential profiles file. " +
                    "Please make sure that your credentials file is at the correct " +
                    "location (C:\\Users\\Sayon\\.aws\\credentials), and is in valid format.",
                    e);
        }
        
        ClientConfiguration clientConfiguration = new ClientConfiguration();
//        clientConfiguration.setProxyHost(proxy_host);
//        clientConfiguration.setProxyPort(proxy_port);
//        clientConfiguration.setProxyUsername(proxy_username);
//        clientConfiguration.setProxyPassword(proxy_password);
//        
        sqs = new AmazonSQSClient(credentials, clientConfiguration);
        Region usWest2 = Region.getRegion(Regions.US_WEST_2);
        sqs.setRegion(usWest2);

	}
	
	public void sendMessage(String messageKeyValue, String textMessage) throws Exception{
	      // Send a message
        System.out.println("Sending a message.\n");
        
        Map<String, MessageAttributeValue> messageAttributes = new HashMap<>();
        messageAttributes.put(message_attribute_name, new MessageAttributeValue().withDataType("String").withStringValue(messageKeyValue));
        
        SendMessageRequest request = new SendMessageRequest();
        request.withMessageBody(textMessage);
        request.withQueueUrl(send_queue_url);
        request.withDelaySeconds(delay_time);
        request.withMessageAttributes(messageAttributes);
        sqs.sendMessage(request);
	}
	
	public List<Message> receiveMessages() throws Exception{
		// Receive messages
        ReceiveMessageRequest receiveMessageRequest = new ReceiveMessageRequest(receive_queue_url);
        receiveMessageRequest.withMessageAttributeNames(message_attribute_name);
        receiveMessageRequest.withAttributeNames(attribute_name);
        receiveMessageRequest.withWaitTimeSeconds(wait_time_seconds);
        receiveMessageRequest.withMaxNumberOfMessages(max_no_messages);
        receiveMessageRequest.withVisibilityTimeout(visibility_timeout);
        
        List<Message> messages = sqs.receiveMessage(receiveMessageRequest).getMessages();
        return messages;
	}
	
	public void deleteMessage(String receiptHandle) throws Exception{
	    // Delete a message
        System.out.println("Deleting a message.\n");
        sqs.deleteMessage(new DeleteMessageRequest(receive_queue_url, receiptHandle));
	}
    
}
