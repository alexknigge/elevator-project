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

    private record Subscription(int topic, int subtopic){}

    private final LinkedList<Message> queue;
    private final Set<Subscription> subscriptions;
    private final Set<Socket> clientSockets;

    private ServerSocket serverSocket;
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    private boolean isServer;
    private int port = 9999;

    public SoftwareBus(boolean isServer) {
        this.isServer = isServer;
        queue = new LinkedList<>();
        subscriptions = new HashSet<>();
        clientSockets = new HashSet<>();

        if (isServer) {
            try {
                serverSocket = new ServerSocket(port);
                acceptThread();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                socket = new Socket("localhost", port);
                out = new PrintWriter(socket.getOutputStream(), true);
                in  = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private void acceptThread() {
        Thread acceptThread = new Thread(() -> {
            try {
                while (true) {
                    Socket newSocket = serverSocket.accept();
                    clientSockets.add(newSocket);
                    System.out.println("Client connected: " + newSocket);
                    readerThread(newSocket);
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        acceptThread.start();
    }

    private void readerThread(Socket socket) {
        Thread readerThread = new Thread(() -> {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String line;
                while ((line = in.readLine()) != null) {
                    System.out.println(line);
                    Message message = Message.parseStringToMsg(line);

                    if (isServer) {
                        synchronized (clientSockets) {
                            for (Socket client : clientSockets) {
                                if (client != socket) {
                                    PrintWriter temp = new PrintWriter(client.getOutputStream(), true);
                                    temp.println(line);
                                }
                            }
                        }
                    } else {
                        synchronized (subscriptions) {
                            for (Subscription s : subscriptions) {
                                if (s.topic() == message.getTopic() &&
                                        (s.subtopic() == 0 || s.subtopic() == message.getSubTopic())) {
                                    synchronized (queue) {
                                        queue.add(message);
                                    }
                                    break;
                                }
                            }
                        }
                    }

                }
            } catch (IOException e) {
                throw new RuntimeException("Connection error in reader thread");
            }
        });
        readerThread.start();
    }


    public void publish(Message message) {
        if (isServer) {
            synchronized (clientSockets) {
                for (Socket client : clientSockets) {
                    try {
                        PrintWriter temp = new PrintWriter(client.getOutputStream(), true);
                        temp.println(message.toString());
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        } else {
            out.println(message.toString());
        }
    }

    public void subscribe(int topic, int subtopic) {
        subscriptions.add(new Subscription(topic, subtopic));
    }

    public Message get(int topic, int subtopic) {
        Iterator<Message> queue_iter = queue.iterator();
        while (queue_iter.hasNext()) {
            Message m = queue_iter.next();
            if (m.getTopic() == topic && (subtopic == 0 || m.getSubTopic() == subtopic)) {
                queue_iter.remove();
                return m;
            }
        }
        return null;
    }
}
