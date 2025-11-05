package Bus;

import Message.Message;

import java.util.Queue;

public class SoftwareBusQueue {
    private SoftwareBus softwareBus;
    private Queue<Message> queue;

    public SoftwareBusQueue(SoftwareBus softwareBus) {
        this.softwareBus = softwareBus;
    }

    /**
     * Get message from queue
     * @param topic Message Topic
     * @param subTopic Message Subtopic
     */
    public void get(int topic, int subTopic) {

    }
}
