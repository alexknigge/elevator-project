public class Message {
    private int topic;
    private String subTopic;
    private String body;

    public Message(int topic, String subTopic, String body) {
        this.topic = topic;
        this.subTopic = subTopic;
        this.body = body;
    }

    public int getTopic() {
        return topic;
    }

    public String getSubTopic() {
        return subTopic;
    }

    public String getBody() {
        return body;
    }
}
