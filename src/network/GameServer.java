package network;

import entities.*;
import generation.*;
import processing.core.PVector;

import java.io.IOException;
import java.net.SocketException;
import java.util.HashMap;
import java.util.Iterator;

public class GameServer extends Server{
    private Room startRoom;
    HashMap<Integer, PlayerData> all_players = new HashMap<>();
    int active_player_count;

    GameServer(String host, int port, int backlog) throws NetworkException {
        super(host, port, backlog);
    }
    GameServer(String host, int port) throws NetworkException{
        super(host, port, 50);
        active_player_count = 0;
    }

    /**
     * Listens for, accepts and registers incoming connections from clients.
     */
    @Override
    public void run(){
        while (!server.isClosed() && active_player_count <= 3) {
            try {
                Connection newConn = new Connection(server.accept());
                System.out.println("Creating a new connection: " + new_client_id);
                connections.put(new_client_id, newConn);
                Message join_message = new Message(Message.JOIN, new_client_id);
                join_message.setRoomData(startRoom.toStub());
                join_message.set_receiver_id(new_client_id);
                newConn.send(join_message);
            } catch (SocketException e) {
                if (!e.getMessage().equals("Socket closed")) {
                    e.printStackTrace();
                }
            } catch (NetworkException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void process_incoming_messages(GameState state) {
        this.collectMessages();

        if (!this.getMessages().isEmpty()){
            Message m = this.getLatestMessage();
            int type = m.getMessage_type();

            if (type == Message.JOIN) {
                System.out.printf("player %d joined\n", new_client_id);
                PVector starting_pos = new PVector(50 * (new_client_id) + 100 * (new_client_id - 1),50);
                all_players.put(new_client_id, new PlayerData(starting_pos));
                state.set_new_player_flag(true);
                new_client_id = new_client_id + 1;
                active_player_count++;
                System.out.println("Active players: " + active_player_count);

            } else if (type == Message.DISCONNECT) {
                System.out.println("Client " + m.getSender_id() + " disconnecting.");
                connections.remove(m.getSender_id());
                all_players.remove(m.getSender_id());
                active_player_count--;
                System.out.println("Active players: " + active_player_count);


            } else if (type == Message.MOVE) {
                all_players.put(m.getSender_id(), m.getPlayerById(m.getSender_id()));
//                System.out.println("Player " + m.getSender_id() + " is now at " + all_players.get(m.getSender_id()).position);
                state.set_player_moved_flag(true);
            }

        }
        return;
    }

    private void next_tick(){
//            for (PlayerData ps : server.all_players.values()) {
//                int room_num = ps.getCurrentRoomNum();
//                Room current_room = gameMap.getRoomById(room_num);
//            }
        return;
    }


    public void broadcast(Message m, GameState s){
        if (s.get_new_player_flag() || s.get_player_moved_flag()) {
            for (Integer i : connections.keySet()){
                broadcastOne(m, i);
            }

            s.set_new_player_flag(false);
            s.set_player_moved_flag(false);
        }
        return;
    }

    public static void main(String[] args) throws InterruptedException{
        String host = "192.168.1.173";
        int port = 10430;
        GameServer server;
        GameState state;
        Map gameMap;

        int new_player_flag = 0;

        // create server object
        try {
            server = new GameServer(host, port);
        } catch (NetworkException e) {
            System.out.println(e.getMessage());
            return;
        }

        // generate the map, and set the start room
        state = new GameState();
        gameMap = new generation.Map();
        Room startRoom = gameMap.getStartRoom();
        server.startRoom = startRoom;


        while (server.active_player_count <= 3) {
            server.process_incoming_messages(state);
            Message m = new Message(Message.MOVE, 0, server.all_players);
            m.set_receiver_id(server.new_client_id);
            server.broadcast(m, state);
//            Thread.sleep(1000);
        }


    }
}
