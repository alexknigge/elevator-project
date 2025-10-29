package DeviceComp;

import Message.Message;
import java.util.List;
import java.util.Map;

public class SoftwareBus {

    //List of connected devices to the Software bus
    private List<Device> connectedDevices;

    //String -> Topic, list of devices that are subscribed to this topic
    private Map<String, List<Device>> topicsToDevices;


    public SoftwareBus() {

    }
    
    private void publish(Message message) {

    }

    private void subscribe(String topic, String subTopic) {

    }
    
    private void get(String topic, String subTopic) {
        
    }
}
