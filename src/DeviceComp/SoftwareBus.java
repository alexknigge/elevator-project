package DeviceComp;

import Message.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SoftwareBus {
    private List<Integer> subscribedTopics;
    private List<String> subscribedSubTopics;

    //TODO No clue how to save messages
    //Messages saved base on topic and subtopic
    private Map<Integer, Map<String, List<Message>>> subscribedMessages;
    //Messages saved based on topic only
    private Map<Integer, List<Message>> topicMessages;

    private final Device device;

    public SoftwareBus(Device device) {
        this.device = device;
        subscribedTopics = new ArrayList<>();
        subscribedSubTopics = new ArrayList<>();
    }

    /**
     * Determines if a message should be saved or not based on this current
     * device subscribed topics
     * @param message Message
     */
    public void handleMessage(Message message) {
        int topicNumber = message.getTopic();
        String subTopic = message.getSubTopic();

        //Do nothing if device isn't subscribed to this message's
        if(!subscribedTopics.contains(topicNumber) || !subscribedSubTopics.contains(subTopic)) {
            return;
        }

        if(topicMessages.containsKey(topicNumber)) {
            List<Message> messages = topicMessages.get(topicNumber);
            if(messages != null) {
                messages.add(message);
            } else {
                messages = new ArrayList<>();
                messages.add(message);
                topicMessages.put(topicNumber, messages);
            }
        } else {
            List<Message> messages = new ArrayList<>();
            messages.add(message);
            topicMessages.put(topicNumber, messages);
        }


        if(subscribedMessages.containsKey(topicNumber)) {
            Map<String, List<Message>> messages = subscribedMessages.get(topicNumber);
            //Check if there is a subtopic
                if(messages != null && messages.containsKey(subTopic)) {
                    List<Message> messageList = messages.get(subTopic);
                    if(messageList != null) {
                        messageList.add(message);
                    } else {
                        messageList = new ArrayList<>();
                        messageList.add(message);
                        messages.put(subTopic, messageList);
                    }
                }
        } else {
            Map<String, List<Message>> messages = new HashMap<>();
            List<Message> messageList = new ArrayList<>();
            messages.put(subTopic, messageList);
            subscribedMessages.put(topicNumber, messages);
        }
    }


    /**
     * Sends a message to the server so that it can be sent out to other
     * devices
     *
     * @param message Message being sent out
     */
    public void publish(Message message) {
        device.sendMessage(message);
    }

    public void subscribe(int topic, String subTopic) {
        subscribedTopics.add(topic);
        subscribedSubTopics.add(subTopic);
    }

    public void get(int topic, String subTopic) {
        //Get message based on topic and subtopic
 	//TODO WHEN WOULD THIS BE CALLED? SEEMS SO STUPID
     	
	// Check if topic exists
    	if (!subscribedMessages.containsKey(topic)) {
            System.out.println("No messages found for topic: " + topic);
            return;
    	}

    	Map<String, List<Message>> subMap = subscribedMessages.get(topic);

    	// Check if subtopic exists
    	if (!subMap.containsKey(subTopic)) {
            System.out.println("No messages found for topic " + topic + " and subtopic: " + subTopic);
            return;
    	}

    	List<Message> messages = subMap.get(subTopic);

    	if (messages == null || messages.isEmpty()) {
            System.out.println("No messages stored for topic " + topic + " and subtopic: " + subTopic);
            return;
    	}

    	// Print all stored messages for that topic/subtopic
    	System.out.println("Messages for topic " + topic + " and subtopic \"" + subTopic + "\":");
    	for (Message msg : messages) {
            System.out.println(msg);
    	}
    }
}
