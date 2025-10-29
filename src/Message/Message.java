package Message;

public class Message {
    private int topic;
    private String subTopic;
    private String body;

    private int deviceID;

    public Message() {
        topic = 1;
        subTopic = null;
        body = null;
    }
    //TODO MAKE EXAMPLE MESSAGES AS WELL AS TOPIC NUMBERS
    public Message(int topic, String subTopic, String body, int deviceID) {
        this.topic = topic;
        this.subTopic = subTopic;
        this.body = body;
        this.deviceID = deviceID;

    }

    @Override
    public String toString() {
        return topic + "-" + subTopic + "-" + body;
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
    public int getDeviceID() {
        return deviceID;
    }

    public void setTopic(int topic) {
        this.topic = topic;
    }

    public void setSubTopic(String subTopic) {
        this.subTopic = subTopic;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setDeviceID(int deviceID) {
        this.deviceID = deviceID;
    }
}
