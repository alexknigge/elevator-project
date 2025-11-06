package Bus;

import Message.Message;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


public class SoftwareBus {

    private record Subscription(int topic, int subtopic) {
    }

    // Local queue for received messages that match this processor's subscriptions
    private final SoftwareBusQueue queue;

    // List of all subscriptions for this bus
    private final Set<Subscription> subscriptions;

    private final SoftwareBusInternalNetwork network;

    public SoftwareBus(boolean isServer) {
        queue = new SoftwareBusQueue();
        subscriptions = new HashSet<>();
        network = new SoftwareBusInternalNetwork(isServer);

        network.setMessageListener(msg -> {
            synchronized (subscriptions) {
                for (Subscription s : subscriptions) {
                    if (s.topic() == msg.getTopic() &&
                            (s.subtopic() == 0 || s.subtopic() == msg.getSubTopic())) {
                        queue.add(msg);
                        break;
                    }
                }
            }

        });
    }

    /**
     * Publishes a message to the bus.
     * - In server mode: broadcast the message to all connected clients.
     * - In client mode: send the message to the central server.
     */
    public void publish(Message message) {
        network.broadcast(message);
    }

    /**
     * Registers a subscription to a given topic and subtopic.
     */
    public void subscribe(int topic, int subtopic) {
        synchronized (subscriptions) {
            subscriptions.add(new Subscription(topic, subtopic));
            System.out.println("subscribed to " + topic + " " + subtopic);
        }
    }

    /**
     * Retrieves and removes the first message in the queue matching the given topic/subtopic.
     * If subtopic = 0, matches all subtopics.
     * Returns null if no matching message is found.
     */
    public Message get(int topic, int subtopic) {
        synchronized (queue) {
            return queue.get(topic, subtopic);
        }
    }

}
