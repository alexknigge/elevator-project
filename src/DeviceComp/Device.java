package DeviceComp;
import Message.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Device implements Runnable {
    private int portNumber;
    private Socket socket;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    //When a device is created, it should immediately connect to the server.

    public Device(int portNumber) {
        this.portNumber = portNumber;
//        try {
//            socket = new Socket("localhost", portNumber);
//
//            out = new ObjectOutputStream(socket.getOutputStream());
//            in = new ObjectInputStream(socket.getInputStream());
//
//        } catch (UnknownHostException e) {
//            throw new RuntimeException(e);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
    }

    /**
     * Send message back to the Software Bus so that it can be sent to
     * subscribed users.
     * @param message
     */
    public void sendMessage(Message message) {
        if(out != null) {
            try {
                out.writeObject(message);
                out.flush();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }


    @Override
    public void run() {
        try {
            while(true) {
                Message message = (Message) in.readObject();
                //When a device receives a message, it should be handled by
                // the software bus

                //TODO NEED TO DECIDE HOW THE MESSAGE SHOULD BE HANDLED BY A DEVICE

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
