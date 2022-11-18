package network;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Message implements Serializable {

    private HashMap<Integer, PlayerData> players = new HashMap<>();
    private ArrayList<ProjectileData> projectiles = new ArrayList();
    private int sender_id;

    private int receiver_id;
    private int message_type;
    private ProjectileData projectileData;
    private RoomData roomData;

    private ArrayList<EnemyData> enemies = new ArrayList();

    public static final int MOVE = 1;
    public static final int JOIN = 0;
    public static final int FIRE = 2;
    public static final int ROOM_CHANGE = 3;
    public static final int UPDATE = 4;
    public static final int DISCONNECT = -1;


    public Message(int _message_type, int _sender_id, HashMap<Integer, PlayerData> _players, ArrayList<ProjectileData> _projectiles){
        players = (HashMap<Integer, PlayerData>) _players.clone();
        sender_id = _sender_id;
        message_type = _message_type;
        projectiles = (ArrayList) _projectiles.clone();
    }

    public Message(int _message_type, int _sender_id, PlayerData _player){
        message_type = _message_type;
        players.put(_sender_id, _player);
        sender_id = _sender_id;
    }

    public Message(int _message_type, int _sender_id){
        message_type = _message_type;
        sender_id = _sender_id;
    }

    public Message(int _message_type, int _sender_id, ProjectileData stub){
        this(_message_type, _sender_id);
        projectileData = stub;

    }

    /** used as a join message **/
    public Message(int _message_type){
        message_type = _message_type;
    }

    public Message(int _message_type, int _sender_id, HashMap<Integer, PlayerData> _players){
        this(_message_type, _sender_id);
        players = (HashMap<Integer, PlayerData>) _players.clone();
    }

    public Message(int _message_type, int _sender_id, HashMap<Integer, PlayerData> _players, RoomData _newRoom, ArrayList<EnemyData> enemyData) {
        message_type = _message_type;
        sender_id = _sender_id;
        players = (HashMap<Integer, PlayerData>) _players.clone();
        roomData = _newRoom;
        enemies = (ArrayList<EnemyData>) enemyData.clone();
    }

    public Message(int _message_type, int _sender_id, RoomData _roomData) {
        message_type = _message_type;
        sender_id = _sender_id;
        roomData = _roomData;
    }


//    public Message(int _message_type, int _sender_id, HashMap<Integer, PlayerData> _players, ArrayList<ProjectileData> projectiles) {
//        message_type = _message_type;
//        sender_id = _sender_id;
//        players = (HashMap<Integer, PlayerData>) _players.clone();
//    }

    public int getSender_id() {
        return sender_id;
    }

    public void set_receiver_id(int receiver_id) {
        this.receiver_id = receiver_id;
    }

    public int get_receiver_id() {
        return receiver_id;
    }

    public PlayerData getPlayerById(int id){
        return players.get(id);
    }

    public int getMessage_type() {
        return message_type;
    }

    public ProjectileData getProjectileData() {
        return projectileData;
    }

    public ArrayList<ProjectileData> getProjectiles() {
        return (ArrayList<ProjectileData>) projectiles.clone();
    }

    @Override
    public String toString() {
        String s = "type: " + message_type + "\nid: " + sender_id + "\n";
        for (PlayerData p : players.values()){
            s += p + "\n";
        }
        return s;
    }

    public RoomData getRoom() {
        return roomData;
    }

    public void setRoomData(RoomData roomData) {
        this.roomData = roomData;
    }

    public HashMap<Integer, PlayerData> getPlayers() {
        return players;
    }

    public ArrayList<EnemyData> getEnemies() {
        return enemies;
    }



}
