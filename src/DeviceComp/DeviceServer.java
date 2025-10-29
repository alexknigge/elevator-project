package DeviceComp;

import Message.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


public class DeviceServer implements Runnable{
    //List of connected devices to the server
    private List<Device> connectedDevices; //TODO NOT SURE IF THIS MAKES
    // SENSE, MAYBE CHANGE TO a DEVICEHANDLER CLASS

    private ServerSocket serverSocket;
    private int portNumber;


    public DeviceServer(int portNumber) throws IOException {
        connectedDevices = new ArrayList<Device>();
        this.portNumber = portNumber;
        serverSocket = new ServerSocket(portNumber);

    }

    @Override
    public void run() {
        System.out.println("Device server running on port number: ");
        try {
            while(true) {
                //Accept client connection
                Socket socket = serverSocket.accept();
                //TODO CONTINUE THIS, probably make some thread that handles
                // messages
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void addDevice(Device device) {
        connectedDevices.add(device);
    }

    public void handleMessage(Message message) {
        //For each device, send the message

    }


}
