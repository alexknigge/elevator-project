package Bus;

import Message.Message;

public interface MessageListener {
    void onMessage(Message message);
}
