package network;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * The class {@code Connection} represents either a connection from server to client or a client end-point in a network
 * {@code Connection}, once connected, is able to exchange data with other party or parties, depending on a server
 * implementation.
 * <br><br>
 * This class is threadsafe.
 *
 * @version 1.0
 * @see Server
 * @see Client
 */
public class Connection implements Runnable {
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Thread thread;

    private ArrayList<Message> messages = new ArrayList<>();

    private final Object writeLock = new Object();

    /**
     * Constructs {@code Connection} using streams of a specified {@link Socket}.
     *
     * @param socket Socket to fetch the streams from.
     */
    public Connection(Socket socket) throws NetworkException {
        if (socket == null) {
            throw new IllegalArgumentException("null socket");
        }

        this.socket = socket;
        try {
            out = new ObjectOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            throw new NetworkException("Could not access output stream.", e);
        }
        try {
            in = new ObjectInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new NetworkException("Could not access input stream.", e);
        }
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Reads messages while connection with the other party is alive.
     */

    @Override
    public void run() {
        while (!socket.isClosed()) {
            try {
                Message m = (Message) in.readObject();

                if (m.getMessage_type() == Message.DISCONNECT) {
                    if (!socket.isClosed()) {
                        System.out.println("Disconnection packet received.");
                        try {
                            close(m.getSender_id());
                        } catch (Exception e) {
                            System.out.println("Unable to disconnect: ");
                            e.printStackTrace();
                        }
                    }
                }
                if (m.getMessage_type() == Message.DISCONNECT) {
                    if (messages.isEmpty()) {
                        messages.add(m);
                    } else {
                        messages.set(0, m);
                    }

                } else if (messages.size() < 50) {
                    messages.add(m);
                }
            } catch (SocketException e) {
                if (!e.getMessage().equals("Socket closed")) {
//                    e.printStackTrace();
                    System.out.println("Connect to server lost");
                    System.exit(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("Unrecognized data received.");
                throw new RuntimeException(e);
            }
        }
    }


    /**
     * Sends data to the other party.
     *
     * @param message Data to send.
     * @throws NetworkException If writing to output stream fails.
     * @throws IllegalStateException If writing data is attempted when connection is closed.
     * @throws IllegalArgumentException If data to send is null.
     * @throws UnsupportedOperationException If unsupported data type is attempted to be sent.
     */
    public void send(Object message) throws NetworkException {
        if (socket.isClosed()) {
            throw new IllegalStateException("Data not sent, connection is closed.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }

        if (!(message instanceof Message)) {
            throw new UnsupportedOperationException("Unsupported data type: " + message.getClass());
        }

        try {
            synchronized (writeLock) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            throw new NetworkException("Data could not be sent.", e);
        }
    }

    /**
     * Sends a disconnection message to, and closes connection with, the other party.
     */
    public void close(int senderId) throws NetworkException {
        if (socket.isClosed()) {
            throw new IllegalStateException("Connection is already closed.");
        }

        try {
            Message message = new Message(Message.DISCONNECT, senderId);
            //problems?
            synchronized (writeLock) {
                out.writeObject(message);
                out.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Disconnection message could not be sent.");
        }

        try {
            synchronized (writeLock) {
                out.close();
            }
        } catch (IOException e) {
            throw new NetworkException("Error while closing connection.", e);
        } finally {
            thread.interrupt();
        }
    }

    /**
     * Returns whether or not the connection to the other party is alive.
     *
     * @return True if connection is alive. False, otherwise.
     */
    public boolean isConnected() {
        return !socket.isClosed();
    }

    @Override
    public String toString(){
        return "ConnectionName";
    }

    public boolean hasMessage(){
        return !messages.isEmpty();
    }

    public Message getLatestMessage(){
        if (messages.isEmpty()) return null;
        return messages.remove(0);
    }
}
