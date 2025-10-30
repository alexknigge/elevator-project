package Message;

public class Message {
    private int topic;
    private int subTopic;
    private String body;

    public Message() {
        topic = 0;
        subTopic = 0;
        body = null;
    }

    //TODO MAKE EXAMPLE MESSAGES AS WELL AS TOPIC NUMBERS
    public Message(int topic, int subTopic, String body) {
        this.topic = topic;
        this.subTopic = subTopic;
        this.body = body;
    }

    @Override
    public String toString() {
        return topic + "-" + subTopic + "-" + body;
    }

    public int getTopic() {
        return topic;
    }

    public int getSubTopic() {
        return subTopic;
    }

    public String getBody() {
        return body;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public void setSubTopic(int subTopic) {
        this.subTopic = subTopic;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public static Message parseStringToMsg(String line) {
        String[] parts = line.split("-", 3);
        int t = Integer.parseInt(parts[0]);
        int st = Integer.parseInt(parts[1]);
        String body = parts[2];
        return new Message(t, st, body);
    }
}
