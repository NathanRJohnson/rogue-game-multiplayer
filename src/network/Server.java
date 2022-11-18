package network;

import entities.Enemy;
import entities.Player;
import generation.Door;
//import generation.Map;
import generation.Room;
import processing.core.PVector;
import tools.Clock;
import tools.Direction;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

/**
 * The class {@code Server} represents a server end-point in a network. {@code Server} once bound to a certain IP
 * address and port, establishes connections with clients and is able to communicate with them or disconnect them.
 * <br><br>
 * This class is threadsafe.
 *
 * @version 1.0
 * @see Client
 * @see Connection
 */
public class Server implements Runnable {
    public ServerSocket server;
    public Map<Integer, Connection> connections;
    public Thread thread;
    public final Object connectionsLock = new Object();
    public ArrayList<Message> messages = new ArrayList<>();

    public int new_client_id;

    public Room startRoom;

    /**
     * Constructs a {@code Server} that interacts with clients on the specified host name and port with the specified
     * requested maximum length of a queue of incoming clients.
     *
     * @param host Host address to use.
     * @param port Port number to use.
     * @param backlog Requested maximum length of the queue of incoming clients.
     * @throws NetworkException If error occurs while starting a server.
     */
    public Server(String host, int port, int backlog) throws NetworkException {
        new_client_id = 1;
        try {
            server = new ServerSocket(port, backlog, InetAddress.getByName(host));
        } catch (UnknownHostException e) {
            throw new NetworkException("Host name could not be resolved: " + host, e);
        } catch (IllegalArgumentException e) {
            throw new NetworkException("Port number needs to be between 0 and 65535 (inclusive): " + port);
        } catch (IOException e) {
            throw new NetworkException("Server could not be started.", e);
        }
        connections = Collections.synchronizedMap(new HashMap<>());
        thread = new Thread(this);
        thread.start();
    }

    /**
     * Constructs a {@code Server} that interacts with clients on the specified host name and port.
     *
     * @param host Host address to bind.
     * @param port Port number to bind.
     * @throws NetworkException If errors occurs while starting a server.
     */
    public Server(String host, int port) throws NetworkException {
        this(host, port, 50);
    }

    /**
     * Listens for, accepts and registers incoming connections from clients.
     */
    @Override
    public void run() {
        while (!server.isClosed()) {
            try {
                Connection newConn = new Connection(server.accept());
                connections.put(new_client_id++, newConn);
            } catch (SocketException e) {
                if (!e.getMessage().equals("Socket closed")) {
                    e.printStackTrace();
                }
            } catch (NetworkException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void collectMessages(){
        for (Connection connection : connections.values()){
            Message message = connection.getLatestMessage();
            if (message != null) {
                messages.add(message);
            }
        }
    }

    /**
     * Pops a message of the message queue
     * @return
     */
    public Message getLatestMessage(){
        if (messages.isEmpty()) return null;
        return messages.remove(0);
    }

    /**
     * Sends data to all registered clients.
     *
     * @param message Message to send.
     * @throws IllegalStateException If writing data is attempted when server is offline.
     * @throws IllegalArgumentException If data to send is null.
     */
    public void broadcast(Message message) {
        if (server.isClosed()) {
            throw new IllegalStateException("Data not sent, server is offline.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }

        synchronized (connectionsLock) {
            for (Connection connection : connections.values()) {
                try {
                    connection.send(message);
//                    System.out.println("Data sent to client successfully.");
                } catch (NetworkException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void broadcastRest(Message message, int skip){
        if (server.isClosed()) {
            throw new IllegalStateException("Data not sent, server is offline.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }
        synchronized (connectionsLock) {
            Connection toSkip = connections.get(skip-1);
            for (Connection connection : connections.values()) {
                try {
                    if (!connection.equals(toSkip)) {
                        connection.send(message);
                    }
                } catch (NetworkException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void broadcastRoom(Message message, int client_id, HashMap<Integer, Room> map){
        if (server.isClosed()) {
            throw new IllegalStateException("Data not sent, server is offline.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }
        synchronized (connectionsLock) {
            Connection toSkip = connections.get(client_id-1);
            int currentRoomId = map.get(client_id).getRoomNum();
            for (Integer i : map.keySet()){
                if (i != client_id && map.get(i).getRoomNum() == currentRoomId){
                    if (!toSkip.equals(connections)) {
                        Connection targetConnection = connections.get(i-1);
                        try {
                            targetConnection.send(message);
                        } catch (NetworkException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    public void broadcastRoomSkip(Message message, int client_id, HashMap<Integer, Room> map){
        if (server.isClosed()) {
            throw new IllegalStateException("Data not sent, server is offline.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }
        synchronized (connectionsLock) {
            int currentRoomId = map.get(client_id).getRoomNum();
            for (Integer i : map.keySet()){
                if (i != client_id && map.get(i).getRoomNum() == currentRoomId){
                    Connection targetConnection = connections.get(i-1);
                    try {
                        targetConnection.send(message);
                    } catch (NetworkException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public void broadcastOne(Message message, int client_id){
        if (server.isClosed()) {
            throw new IllegalStateException("Data not sent, server is offline.");
        }
        if (message == null) {
            throw new IllegalArgumentException("null data");
        }
        synchronized (connectionsLock) {
            Connection targetConnection = connections.get(client_id);
            if (targetConnection.isConnected()) {
                try {
                    targetConnection.send(message);
                } catch (NetworkException e) {
                    System.out.println("Message not sent, connection lost.");
                }
            }
        }
    }

    /**
     * Sends a disconnection message and disconnects specified client.
     *
     * @param connection Client to disconnect.
     * @throws NetworkException If error occurs while closing connection.
     */
    public void disconnect(Connection connection) throws NetworkException {
        if (connections.containsValue(connection)) {
            connection.close(0);
        }
    }

    /**
     * Sends a disconnection message to all clients, disconnects them and terminates the server.
     */
    public void close() throws NetworkException {
        synchronized (connectionsLock) {
            for (Connection connection : connections.values()) {
                try {
                    connection.close(0);
                } catch (NetworkException e) {
                    e.printStackTrace();
                }
            }
        }
        connections.clear();

        try {
            server.close();
        } catch (IOException e) {
            throw new NetworkException("Error while closing server.");
        } finally {
            thread.interrupt();
        }
    }

    /**
     * Returns whether or not the server is online.
     *
     * @return True if server is online. False, otherwise.
     */
    public boolean isOnline() {
        return !server.isClosed();
    }

    /**
     * Returns an array of registered clients.
     */
    public Connection[] getConnections() {
        synchronized (connectionsLock) {
            return connections.values().toArray(new Connection[connections.size()]);
        }
    }

    public ArrayList getMessages() {
        return messages;
    }


    public static void main(String[] args) throws NetworkException, InterruptedException, UnknownHostException {
        String host = "10.0.1.3";
//        String host = "192.168.2.24";
        int port = 10430;
        int update = 0;

        Server server = new Server(host, port);
        System.out.println("Server started");

        //Server side game entities
        HashMap<Integer, PlayerData> all_players = new HashMap<>();
        HashMap<Integer, PlayerData> current_viewable_players = new HashMap<>();
        HashMap<Integer, Room> playerInRoomMap = new HashMap<>();
        HashMap<Integer, ArrayList<EnemyData>> enemiesInRoom = new HashMap<>();
        ArrayList<ProjectileData> projectiles = new ArrayList<>();
        Clock gameClock = new Clock();
        generation.Map gameMap = new generation.Map();

        gameMap.buildMapGraph();
        Room startRoom = gameMap.getStartRoom();
        Room currentRoom = startRoom;
        currentRoom.setHasSpawned(true);
        currentRoom.unlockDoors();
        server.startRoom = startRoom;

        while (server.connections.size() < 5) {

            if (server.connections.size() > 0 && update != 0) {
                //if there's an update, send the update message to all players
//                for (int i = 1; i <= all_players.size(); i++){
//                    //if it's not me and they are in my current room
//                    if (i != update && currentRoom.getRoomNum() == playerInRoomMap.get(i).getRoomNum()){
//                        System.out.println("Adding " + all_players.get(i) + " to viewable players");
//                        current_viewable_players.put(i, all_players.get(i));
//                    }
//                }
//                Message outgoing = new Message(Message.UPDATE, update, current_viewable_players);
//                outgoing.setNumPlayers(all_players.size());
//                server.broadcastRoom(outgoing, update, playerInRoomMap);
                projectiles.clear();
                update = 0;
            }

            //room routine

            //Door routine
            int player_id = 1;
            for (PlayerData ps : all_players.values()){
                currentRoom = playerInRoomMap.get(player_id);
                for (Door d : currentRoom.getDoorsMap().values()) {
                    Player p = Player.createPlayerFromStub(ps);
                    if (!d.isLocked() && d.isEntered(p)) {
                        System.out.println("Player " + player_id + " has entered a door");
                        Direction card_dir = d.getDirection();
                        p.setPosByCompass(card_dir);
                        currentRoom = currentRoom.getRoomAt(card_dir);
                        all_players.put(player_id, p.toPData());
                        playerInRoomMap.put(player_id, currentRoom);
                        current_viewable_players.clear();

                        //add all other players currently in this room to viewable players
                        for (int i = 1; i <= all_players.size(); i++){
                            //if in the same room -- need to send self as well here unfortunately
                            if (currentRoom.getRoomNum() == playerInRoomMap.get(i).getRoomNum()){
                                current_viewable_players.put(i, all_players.get(i));
                            }
                        }

                        //get enemies
                        //if this room hasn't been loaded then load the room
                        //otherwise we can just use the map to look up
                        if (!enemiesInRoom.containsKey(currentRoom.getRoomNum())){
                            System.out.println("Initializing room");
//                        currentRoom.initRoom();
                            ArrayList<EnemyData> enemiesData = new ArrayList<>();
                            for (Enemy e : currentRoom.getEnemies()){
                                System.out.println("Adding enemy --");
                                enemiesData.add(e.toStub());
                            }
                            System.out.println("There are " + enemiesData.size() + " baddies");
                            enemiesInRoom.put(currentRoom.getRoomNum(), enemiesData);
                        }


                        Message message = new Message(Message.ROOM_CHANGE, player_id, current_viewable_players, playerInRoomMap.get(player_id).toStub(), enemiesInRoom.get(currentRoom.getRoomNum()));
                        server.broadcastOne(message, player_id);
                    }
                }
                player_id++;
            }

            //TODO: remove line when I add enemies back in
            currentRoom.unlockDoors();
            server.collectMessages();

            if (!server.getMessages().isEmpty()) {
                Message m = server.getLatestMessage();
                int messageType = m.getMessage_type();

                if (messageType == Message.JOIN){
                    System.out.println("A player has joined");
                    int numPlayers = server.connections.size();
                    PVector start_pos = new PVector(50 * numPlayers + 100 * (numPlayers - 1),50);
                    all_players.put(numPlayers, new PlayerData(start_pos));
                    playerInRoomMap.put(numPlayers, startRoom);
                    for (int i = 1; i < all_players.size(); i++){
                    //if it's not me and they are in my current room
                    if (playerInRoomMap.get(i).getRoomNum() == startRoom.getRoomNum()){
                        System.out.println("Adding " + all_players.get(i) + " to viewable players");
                        current_viewable_players.put(i, all_players.get(i));
                        }
                    }
                    update = numPlayers;
                }

                else if (messageType == Message.DISCONNECT){
//                    server.disconnect(server.connections.get(update));
                    Iterator<Connection> it = server.connections.values().iterator();
                    while (it.hasNext()) {
                        Connection c = (Connection) it.next();
                        if (!c.isConnected()) {
                            all_players.remove(m.getSender_id());
                            System.out.println("closing + removing connection: " + update);
                            it.remove();
                        }
                    }
                }

                else if (messageType == Message.MOVE) {
                    // get the location and update the server locations list
                    PlayerData player = m.getPlayerById(m.getSender_id());
                    update = m.getSender_id();
                    all_players.put(update, player);
                    current_viewable_players.clear();
                    current_viewable_players.put(update, player);
                    Message outgoing = new Message(Message.MOVE, update, current_viewable_players, projectiles);
//                    outgoing.setNumPlayers(all_players.size());
                    server.broadcastRoom(outgoing, m.getSender_id(), playerInRoomMap);
                }

                else if (messageType == Message.ROOM_CHANGE){
                    // get the visible players list
                    for (int i = 1; i <= all_players.size(); i++){
                        current_viewable_players.clear();
                        for (int j = 1; j <= all_players.size(); j++) {
                            if (i != j && playerInRoomMap.get(i).getRoomNum() == playerInRoomMap.get(j).getRoomNum()){
                                current_viewable_players.put(i, all_players.get(i));
                            }
                        }
                            server.broadcastRest(new Message(Message.UPDATE, i, current_viewable_players, currentRoom.toStub(), enemiesInRoom.get(currentRoom.getRoomNum())), m.getSender_id());
                    }
                }

                else if (messageType == Message.FIRE){
                    update = m.getSender_id();
                    projectiles.add(m.getProjectileData());
                }
            }
        }


        Thread.sleep(1 * 1000L);


//        while (server.getConnections().length > 0) {
//            server.disconnect(server.getConnections()[0]);
//            Thread.sleep(50L);
//        }

        // or client.close() to disconnect from client-side
        System.out.println("Closing server");
        server.close();
    }



}